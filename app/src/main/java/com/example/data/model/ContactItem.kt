package com.example.data.model

data class PhoneNumberItem(
    val number: String,
    val label: String,
    val isPrimary: Boolean = false
)

data class ContactItem(
    val id: String,
    val lookupKey: String,
    val displayName: String,
    val devicePhotoUri: String? = null,
    val customPhotoPath: String? = null,
    val phoneNumbers: List<PhoneNumberItem> = emptyList(),
    val isFavorite: Boolean = false,
    val isEmergency: Boolean = false
) {
    /**
     * Resolves the highest priority display photo (custom caregiver photo override first, then Android contact photo)
     */
    val effectivePhotoUri: String?
        get() = customPhotoPath ?: devicePhotoUri

    val primaryNumber: String?
        get() = phoneNumbers.firstOrNull { it.isPrimary }?.number ?: phoneNumbers.firstOrNull()?.number
}
