package com.example.service

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat

object CallService {

    /**
     * Attempts to initiate a phone call.
     * Uses ACTION_CALL if permission is granted, otherwise gracefully falls back to ACTION_DIAL.
     * Returns true if an intent was successfully launched.
     */
    fun initiateCall(context: Context, rawNumber: String): Boolean {
        val cleanNumber = rawNumber.trim()
        if (cleanNumber.isEmpty()) {
            Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
            return false
        }

        val encodedNumber = Uri.encode(cleanNumber)
        val telUri = Uri.parse("tel:$encodedNumber")

        val hasCallPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED

        if (hasCallPermission) {
            try {
                val callIntent = Intent(Intent.ACTION_CALL, telUri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(callIntent)
                return true
            } catch (e: Exception) {
                // Fall back to ACTION_DIAL
            }
        }

        // Safe fallback: ACTION_DIAL (never requires CALL_PHONE permission)
        try {
            val dialIntent = Intent(Intent.ACTION_DIAL, telUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(dialIntent)
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open phone dialer", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}
