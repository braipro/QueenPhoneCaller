package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.QueenPhoneDatabase
import com.example.data.model.CallRecord
import com.example.data.model.ContactItem
import com.example.data.repository.AppSettings
import com.example.data.repository.CallHistoryRepository
import com.example.data.repository.ContactsRepository
import com.example.data.repository.SettingsRepository
import com.example.service.CallService
import com.example.service.SpeechService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class QueenNavTab {
    HOME,
    FAVORITES,
    RECENTS,
    KEYPAD,
    SETTINGS
}

data class QueenUiState(
    val currentTab: QueenNavTab = QueenNavTab.HOME,
    val contacts: List<ContactItem> = emptyList(),
    val isLoadingContacts: Boolean = false,
    val hasContactsPermission: Boolean = false,
    val selectedContact: ContactItem? = null,
    val keypadNumber: String = "",
    val searchQuery: String = "",
    val voiceListening: Boolean = false
)

class QueenViewModel(application: Application) : AndroidViewModel(application) {

    private val database = QueenPhoneDatabase.getDatabase(application)
    private val contactsRepository = ContactsRepository(application, database.contactPreferenceDao())
    private val callHistoryRepository = CallHistoryRepository(database.callRecordDao())
    private val settingsRepository = SettingsRepository(application)
    private val speechService = SpeechService(application)

    private val _uiState = MutableStateFlow(QueenUiState())
    val uiState: StateFlow<QueenUiState> = _uiState.asStateFlow()

    val settings: StateFlow<AppSettings> = settingsRepository.settings

    val recentCalls: StateFlow<List<CallRecord>> = callHistoryRepository.recentCalls
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Collect preferences and reload contacts when updated
        viewModelScope.launch {
            database.contactPreferenceDao().getAllPreferences().collect {
                if (_uiState.value.hasContactsPermission) {
                    loadContacts()
                }
            }
        }
    }

    fun onPermissionStatusChanged(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasContactsPermission = granted)
        if (granted) {
            loadContacts()
        }
    }

    fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingContacts = true)
            val list = contactsRepository.getContacts()
            _uiState.value = _uiState.value.copy(
                contacts = list,
                isLoadingContacts = false,
                // Update selectedContact if currently open
                selectedContact = _uiState.value.selectedContact?.let { selected ->
                    list.find { it.id == selected.id } ?: selected
                }
            )
        }
    }

    fun setNavTab(tab: QueenNavTab) {
        _uiState.value = _uiState.value.copy(
            currentTab = tab,
            selectedContact = null // Close confirmation screen when switching tabs
        )
    }

    fun selectContact(contact: ContactItem) {
        _uiState.value = _uiState.value.copy(selectedContact = contact)
        if (settings.value.speakNamesAloud && contact.displayName.isNotBlank()) {
            speechService.speakName(contact.displayName)
        }
    }

    fun clearSelectedContact() {
        _uiState.value = _uiState.value.copy(selectedContact = null)
    }

    fun makeCall(context: Context, contact: ContactItem, number: String) {
        val success = CallService.initiateCall(context, number)
        if (success) {
            viewModelScope.launch {
                callHistoryRepository.recordCall(
                    contactId = contact.id,
                    contactName = contact.displayName,
                    phoneNumber = number,
                    photoUri = contact.effectivePhotoUri
                )
            }
        }
    }

    fun makeKeypadCall(context: Context) {
        val number = _uiState.value.keypadNumber.trim()
        if (number.isNotEmpty()) {
            val matchingContact = _uiState.value.contacts.firstOrNull { c ->
                c.phoneNumbers.any { it.number.replace(Regex("[^0-9+]"), "") == number.replace(Regex("[^0-9+]"), "") }
            }
            val success = CallService.initiateCall(context, number)
            if (success) {
                viewModelScope.launch {
                    callHistoryRepository.recordCall(
                        contactId = matchingContact?.id,
                        contactName = matchingContact?.displayName ?: "Number $number",
                        phoneNumber = number,
                        photoUri = matchingContact?.effectivePhotoUri
                    )
                }
            }
        }
    }

    fun appendKeypadDigit(digit: String) {
        val current = _uiState.value.keypadNumber
        if (current.length < 24) {
            _uiState.value = _uiState.value.copy(keypadNumber = current + digit)
        }
    }

    fun deleteKeypadDigit() {
        val current = _uiState.value.keypadNumber
        if (current.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(keypadNumber = current.dropLast(1))
        }
    }

    fun clearKeypad() {
        _uiState.value = _uiState.value.copy(keypadNumber = "")
    }

    fun toggleFavorite(contact: ContactItem) {
        viewModelScope.launch {
            contactsRepository.toggleFavorite(contact.id, contact.isFavorite)
            loadContacts()
        }
    }

    fun toggleEmergency(contact: ContactItem) {
        viewModelScope.launch {
            contactsRepository.toggleEmergency(contact.id, contact.isEmergency)
            loadContacts()
        }
    }

    fun saveCustomPhoto(contactId: String, uri: Uri) {
        viewModelScope.launch {
            contactsRepository.saveCustomPhoto(contactId, uri)
            loadContacts()
        }
    }

    fun resetCustomPhoto(contactId: String) {
        viewModelScope.launch {
            contactsRepository.resetCustomPhoto(contactId)
            loadContacts()
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onVoiceSearchResult(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        val cleanQuery = query.trim().lowercase()
        // Try to match an exact or prefix contact
        val match = _uiState.value.contacts.firstOrNull {
            it.displayName.lowercase().contains(cleanQuery)
        }
        if (match != null) {
            selectContact(match)
        }
    }

    // Caregiver settings actions
    fun updateSimplifiedMode(enabled: Boolean) {
        settingsRepository.updateSimplifiedMode(enabled)
    }

    fun updateSpeakNamesAloud(enabled: Boolean) {
        settingsRepository.updateSpeakNamesAloud(enabled)
    }

    fun updateGridColumns(columns: Int) {
        settingsRepository.updateGridColumns(columns)
    }

    fun updateDarkThemeMode(mode: Int) {
        settingsRepository.updateDarkThemeMode(mode)
    }

    fun resetDefaults() {
        settingsRepository.resetDefaults()
        clearKeypad()
    }

    fun clearCallHistory() {
        viewModelScope.launch {
            callHistoryRepository.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechService.shutdown()
    }
}
