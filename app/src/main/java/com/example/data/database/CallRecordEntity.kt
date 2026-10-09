package com.example.data.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "call_history")
data class CallRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactId: String? = null,
    val contactName: String,
    val phoneNumber: String,
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val callType: String = "OUTGOING"
)

@Dao
interface CallRecordDao {
    @Query("SELECT * FROM call_history ORDER BY timestamp DESC LIMIT 100")
    fun getRecentCalls(): Flow<List<CallRecordEntity>>

    @Insert
    suspend fun insertCall(call: CallRecordEntity): Long

    @Query("DELETE FROM call_history")
    suspend fun clearHistory()
}
