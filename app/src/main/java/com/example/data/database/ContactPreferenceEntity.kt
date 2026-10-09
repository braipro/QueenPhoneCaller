package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_preferences")
data class ContactPreferenceEntity(
    @PrimaryKey
    val contactId: String,
    val customPhotoPath: String? = null,
    val isFavorite: Boolean = false,
    val isEmergency: Boolean = false
)
