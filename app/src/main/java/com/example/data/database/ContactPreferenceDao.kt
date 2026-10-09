package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactPreferenceDao {
    @Query("SELECT * FROM contact_preferences")
    fun getAllPreferences(): Flow<List<ContactPreferenceEntity>>

    @Query("SELECT * FROM contact_preferences WHERE contactId = :contactId")
    suspend fun getPreference(contactId: String): ContactPreferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(preference: ContactPreferenceEntity)

    @Query("UPDATE contact_preferences SET isFavorite = :isFavorite WHERE contactId = :contactId")
    suspend fun updateFavorite(contactId: String, isFavorite: Boolean)

    @Query("UPDATE contact_preferences SET customPhotoPath = :customPhotoPath WHERE contactId = :contactId")
    suspend fun updateCustomPhoto(contactId: String, customPhotoPath: String?)

    @Query("UPDATE contact_preferences SET isEmergency = :isEmergency WHERE contactId = :contactId")
    suspend fun updateEmergency(contactId: String, isEmergency: Boolean)
}
