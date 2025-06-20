package com.example.haleemahzahrainventoryapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SmsPermissionActivity : AppCompatActivity() {

    private val SMS_PERMISSION_CODE = 123
    private val phoneNumber = "1234567890"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_permission)

        val btnGrant = findViewById<Button>(R.id.btnGrantPermission)
        val btnSkip = findViewById<Button>(R.id.btnSkip)

        // Grant permission button
        btnGrant.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS),
                    SMS_PERMISSION_CODE
                )
            } else {
                sendSmsNotification()
                goToDashboard()
            }
        }

        // Skip button
        btnSkip.setOnClickListener {
            goToDashboard()
        }
    }

    // Method to send SMS notification
    private fun sendSmsNotification() {
        val smsManager = SmsManager.getDefault()
        val message = "SMS permission enabled. You will now receive low inventory alerts."
        try {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(this, "SMS sent successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show()
        }
    }

    // Navigate to the InventoryActivity
    private fun goToDashboard() {
        startActivity(Intent(this, InventoryActivity::class.java))
        finish()
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send SMS and go to dashboard
                sendSmsNotification()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied. You will not receive SMS notifications.", Toast.LENGTH_LONG).show()
            }
            goToDashboard()
        }
    }
}
