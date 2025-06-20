package com.example.haleemahzahrainventoryapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

object SmsUtil {
    private const val PHONE_NUMBER = "1234567890"
    private const val TAG = "SmsUtil"

    fun sendLowInventoryAlert(context: Context, itemName: String) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return
        }

        val smsManager = SmsManager.getDefault()
        val message = "ALERT: Item \"$itemName\" is out of stock!"

        try {
            smsManager.sendTextMessage(PHONE_NUMBER, null, message, null, null)
            Toast.makeText(context, "Low inventory SMS alert sent.", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "SMS sent successfully for item: $itemName")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to send SMS alert.", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Error sending SMS: ${e.message}")
        }
    }
}
