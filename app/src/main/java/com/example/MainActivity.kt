package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.QueenNavTab
import com.example.ui.QueenViewModel
import com.example.ui.components.QueenBottomNav
import com.example.ui.components.QueenTopBar
import com.example.ui.screens.ContactConfirmScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KeypadScreen
import com.example.ui.screens.RecentsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.QueenPhoneTheme

class MainActivity : ComponentActivity() {

    private val viewModel: QueenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val recentCalls by viewModel.recentCalls.collectAsStateWithLifecycle()

            val isDarkTheme = when (settings.darkThemeMode) {
                2 -> true
                1 -> false
                else -> isSystemInDarkTheme()
            }

            QueenPhoneTheme(darkTheme = isDarkTheme) {
                QueenAppContent(
                    viewModel = viewModel,
                    uiState = uiState,
                    settings = settings,
                    recentCalls = recentCalls
                )
            }
        }
    }
}

@Composable
fun QueenAppContent(
    viewModel: QueenViewModel,
    uiState: com.example.ui.QueenUiState,
    settings: com.example.data.repository.AppSettings,
    recentCalls: List<com.example.data.model.CallRecord>
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Contacts permission launcher
    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionStatusChanged(isGranted)
    }

    // Voice recognition launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                viewModel.onVoiceSearchResult(spoken)
            }
        }
    }

    // Check contact permission on launch
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        viewModel.onPermissionStatusChanged(hasPermission)
    }

    // Refresh contacts on app resume if permission is already granted
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
                viewModel.onPermissionStatusChanged(hasPermission)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Show top bar only when not on individual contact confirmation screen or settings screen
            if (uiState.selectedContact == null && uiState.currentTab != QueenNavTab.SETTINGS) {
                QueenTopBar(
                    onVoiceClick = {
                        try {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a contact's name…")
                            }
                            speechLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Voice search is not available on this device", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onSettingsClick = {
                        viewModel.setNavTab(QueenNavTab.SETTINGS)
                    },
                    simplifiedMode = settings.simplifiedMode
                )
            }
        },
        bottomBar = {
            // Show bottom navigation bar when not on contact confirmation screen or settings
            if (uiState.selectedContact == null && uiState.currentTab != QueenNavTab.SETTINGS) {
                QueenBottomNav(
                    currentTab = uiState.currentTab,
                    onTabSelected = { tab ->
                        viewModel.setNavTab(tab)
                    },
                    simplifiedMode = settings.simplifiedMode
                )
            }
        }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        // SCREEN ROUTING
        when {
            // Contact Confirmation Screen (Highest priority if contact selected)
            uiState.selectedContact != null -> {
                ContactConfirmScreen(
                    contact = uiState.selectedContact,
                    onCallClick = { contact, number ->
                        viewModel.makeCall(context, contact, number)
                    },
                    onBackClick = {
                        viewModel.clearSelectedContact()
                    },
                    onToggleFavorite = { contact ->
                        viewModel.toggleFavorite(contact)
                    },
                    onToggleEmergency = { contact ->
                        viewModel.toggleEmergency(contact)
                    },
                    onCustomPhotoSelected = { contactId, uri ->
                        viewModel.saveCustomPhoto(contactId, uri)
                    },
                    onResetPhoto = { contactId ->
                        viewModel.resetCustomPhoto(contactId)
                    },
                    simplifiedMode = settings.simplifiedMode,
                    modifier = contentModifier
                )
            }

            // Caregiver Settings Screen
            uiState.currentTab == QueenNavTab.SETTINGS -> {
                SettingsScreen(
                    settings = settings,
                    onSimplifiedModeChange = { viewModel.updateSimplifiedMode(it) },
                    onSpeakNamesChange = { viewModel.updateSpeakNamesAloud(it) },
                    onGridColumnsChange = { viewModel.updateGridColumns(it) },
                    onDarkThemeModeChange = { viewModel.updateDarkThemeMode(it) },
                    onResetDefaults = { viewModel.resetDefaults() },
                    onBackClick = { viewModel.setNavTab(QueenNavTab.HOME) },
                    modifier = contentModifier
                )
            }

            // Favorites Tab
            uiState.currentTab == QueenNavTab.FAVORITES -> {
                FavoritesScreen(
                    contacts = uiState.contacts,
                    onContactClick = { contact ->
                        viewModel.selectContact(contact)
                    },
                    gridColumns = settings.gridColumns,
                    simplifiedMode = settings.simplifiedMode,
                    modifier = contentModifier
                )
            }

            // Recent Calls Tab
            uiState.currentTab == QueenNavTab.RECENTS -> {
                RecentsScreen(
                    recentCalls = recentCalls,
                    contacts = uiState.contacts,
                    onContactClick = { contact ->
                        viewModel.selectContact(contact)
                    },
                    onDirectCall = { ctx, number ->
                        val matchingContact = uiState.contacts.firstOrNull { c ->
                            c.phoneNumbers.any { it.number.replace(Regex("[^0-9+]"), "") == number.replace(Regex("[^0-9+]"), "") }
                        }
                        if (matchingContact != null) {
                            viewModel.makeCall(ctx, matchingContact, number)
                        } else {
                            val dummy = com.example.data.model.ContactItem(
                                id = "",
                                lookupKey = "",
                                displayName = number
                            )
                            viewModel.makeCall(ctx, dummy, number)
                        }
                    },
                    onClearHistory = {
                        viewModel.clearCallHistory()
                    },
                    simplifiedMode = settings.simplifiedMode,
                    modifier = contentModifier
                )
            }

            // Keypad Tab
            uiState.currentTab == QueenNavTab.KEYPAD -> {
                KeypadScreen(
                    number = uiState.keypadNumber,
                    onDigitClick = { digit ->
                        viewModel.appendKeypadDigit(digit)
                    },
                    onDeleteClick = {
                        viewModel.deleteKeypadDigit()
                    },
                    onCallClick = { ctx ->
                        viewModel.makeKeypadCall(ctx)
                    },
                    modifier = contentModifier
                )
            }

            // Home Tab (Visual Contact Grid)
            else -> {
                HomeScreen(
                    contacts = uiState.contacts,
                    isLoading = uiState.isLoadingContacts,
                    hasPermission = uiState.hasContactsPermission,
                    onRequestPermission = {
                        contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    },
                    onContactClick = { contact ->
                        viewModel.selectContact(contact)
                    },
                    gridColumns = settings.gridColumns,
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    simplifiedMode = settings.simplifiedMode,
                    modifier = contentModifier
                )
            }
        }
    }
}
