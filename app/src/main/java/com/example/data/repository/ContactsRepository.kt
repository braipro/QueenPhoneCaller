package com.example.data.repository

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import com.example.data.database.ContactPreferenceDao
import com.example.data.database.ContactPreferenceEntity
import com.example.data.model.ContactItem
import com.example.data.model.PhoneNumberItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ContactsRepository(
    private val context: Context,
    private val preferenceDao: ContactPreferenceDao
) {

    /**
     * Reads contacts from Android ContactsContract and merges with stored preferences.
     */
    suspend fun getContacts(): List<ContactItem> = withContext(Dispatchers.IO) {
        val rawContactsMap = mutableMapOf<String, RawContactBuilder>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.LABEL,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.IS_PRIMARY
        )

        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC"
            )

            cursor?.use { c ->
                val idIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val lookupIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)
                val nameIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
                val numberIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val typeIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
                val labelIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL)
                val photoIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                val isPrimaryIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY)

                while (c.moveToNext()) {
                    val contactId = if (idIdx >= 0) c.getString(idIdx) ?: "" else ""
                    if (contactId.isEmpty()) continue

                    val lookupKey = if (lookupIdx >= 0) c.getString(lookupIdx) ?: contactId else contactId
                    val name = if (nameIdx >= 0) c.getString(nameIdx) ?: "Contact" else "Contact"
                    val number = if (numberIdx >= 0) c.getString(numberIdx) ?: "" else ""
                    val photoUri = if (photoIdx >= 0) c.getString(photoIdx) else null
                    val isPrimary = if (isPrimaryIdx >= 0) c.getInt(isPrimaryIdx) != 0 else false
                    val type = if (typeIdx >= 0) c.getInt(typeIdx) else ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
                    val customLabel = if (labelIdx >= 0) c.getString(labelIdx) else null

                    val label = when (type) {
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> "Mobile"
                        ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> "Home"
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> "Work"
                        else -> customLabel ?: "Phone"
                    }

                    val builder = rawContactsMap.getOrPut(contactId) {
                        RawContactBuilder(
                            id = contactId,
                            lookupKey = lookupKey,
                            displayName = name,
                            devicePhotoUri = photoUri
                        )
                    }

                    if (number.isNotBlank()) {
                        builder.numbers.add(
                            PhoneNumberItem(
                                number = number.trim(),
                                label = label,
                                isPrimary = isPrimary
                            )
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            // Permission not granted or revoked
            return@withContext emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }

        // Query contact preferences from Room
        val contactItems = rawContactsMap.values.map { raw ->
            val pref = preferenceDao.getPreference(raw.id)
            ContactItem(
                id = raw.id,
                lookupKey = raw.lookupKey,
                displayName = raw.displayName,
                devicePhotoUri = raw.devicePhotoUri,
                customPhotoPath = pref?.customPhotoPath,
                phoneNumbers = raw.numbers.distinctBy { it.number },
                isFavorite = pref?.isFavorite ?: false,
                isEmergency = pref?.isEmergency ?: false
            )
        }

        // Sort: Emergency first, then Favorites, then Alphabetical
        contactItems.sortedWith(
            compareByDescending<ContactItem> { it.isEmergency }
                .thenByDescending { it.isFavorite }
                .thenBy { it.displayName.lowercase() }
        )
    }

    suspend fun toggleFavorite(contactId: String, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        val existing = preferenceDao.getPreference(contactId)
        if (existing == null) {
            preferenceDao.insertOrUpdate(
                ContactPreferenceEntity(
                    contactId = contactId,
                    isFavorite = !currentFavorite
                )
            )
        } else {
            preferenceDao.updateFavorite(contactId, !currentFavorite)
        }
    }

    suspend fun toggleEmergency(contactId: String, currentEmergency: Boolean) = withContext(Dispatchers.IO) {
        val existing = preferenceDao.getPreference(contactId)
        if (existing == null) {
            preferenceDao.insertOrUpdate(
                ContactPreferenceEntity(
                    contactId = contactId,
                    isEmergency = !currentEmergency
                )
            )
        } else {
            preferenceDao.updateEmergency(contactId, !currentEmergency)
        }
    }

    /**
     * Saves a caregiver-selected photo URI to local internal storage so it is persistent across reboots.
     */
    suspend fun saveCustomPhoto(contactId: String, sourceUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val photosDir = File(context.filesDir, "custom_contact_photos")
            if (!photosDir.exists()) {
                photosDir.mkdirs()
            }
            val destinationFile = File(photosDir, "contact_${contactId}.jpg")

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }

            val savedPath = destinationFile.absolutePath

            val existing = preferenceDao.getPreference(contactId)
            if (existing == null) {
                preferenceDao.insertOrUpdate(
                    ContactPreferenceEntity(
                        contactId = contactId,
                        customPhotoPath = savedPath
                    )
                )
            } else {
                preferenceDao.updateCustomPhoto(contactId, savedPath)
            }

            savedPath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun resetCustomPhoto(contactId: String) = withContext(Dispatchers.IO) {
        try {
            val photosDir = File(context.filesDir, "custom_contact_photos")
            val file = File(photosDir, "contact_${contactId}.jpg")
            if (file.exists()) {
                file.delete()
            }
            preferenceDao.updateCustomPhoto(contactId, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private class RawContactBuilder(
        val id: String,
        val lookupKey: String,
        val displayName: String,
        val devicePhotoUri: String?,
        val numbers: MutableList<PhoneNumberItem> = mutableListOf()
    )
}
