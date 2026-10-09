package com.example.data.model

data class CallRecord(
    val id: Long = 0,
    val contactId: String? = null,
    val contactName: String,
    val phoneNumber: String,
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val type: CallType = CallType.OUTGOING
)

enum class CallType {
    OUTGOING,
    INCOMING,
    MISSED
}
