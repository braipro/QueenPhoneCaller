package com.example.data.repository

import com.example.data.database.CallRecordDao
import com.example.data.database.CallRecordEntity
import com.example.data.model.CallRecord
import com.example.data.model.CallType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CallHistoryRepository(
    private val callRecordDao: CallRecordDao
) {
    val recentCalls: Flow<List<CallRecord>> = callRecordDao.getRecentCalls().map { entities ->
        entities.map { entity ->
            CallRecord(
                id = entity.id,
                contactId = entity.contactId,
                contactName = entity.contactName,
                phoneNumber = entity.phoneNumber,
                photoUri = entity.photoUri,
                timestamp = entity.timestamp,
                type = when (entity.callType) {
                    "INCOMING" -> CallType.INCOMING
                    "MISSED" -> CallType.MISSED
                    else -> CallType.OUTGOING
                }
            )
        }
    }

    suspend fun recordCall(
        contactId: String?,
        contactName: String,
        phoneNumber: String,
        photoUri: String?
    ) = withContext(Dispatchers.IO) {
        callRecordDao.insertCall(
            CallRecordEntity(
                contactId = contactId,
                contactName = contactName,
                phoneNumber = phoneNumber,
                photoUri = photoUri,
                timestamp = System.currentTimeMillis(),
                callType = "OUTGOING"
            )
        )
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        callRecordDao.clearHistory()
    }
}
