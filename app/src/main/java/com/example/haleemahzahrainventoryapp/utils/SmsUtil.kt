package com.example.haleemahzahrainventoryapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat

object SmsUtil {
    private const val PHONE_NUMBER = "1234567890"
    private const val TAG = "SmsUtil"

    fun sendLowInventoryAlert(context: Context, itemName: String): Boolean {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            Log.w(TAG, "SMS permission not granted")
            return false
        }

        val smsManager = SmsManager.getDefault()
        val message = "ALERT: Item \"$itemName\" is out of stock!"

        return try {
            smsManager.sendTextMessage(PHONE_NUMBER, null, message, null, null)
            Log.d(TAG, "SMS sent successfully for item: $itemName")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error sending SMS: ${e.message}")
            false
        }
    }
}
