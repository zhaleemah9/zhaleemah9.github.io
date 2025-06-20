package com.example.haleemahzahrainventoryapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        btnLogin.setOnClickListener {
            val email = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, SmsPermissionActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        btnRegister.setOnClickListener {
            showCreateAccountDialog()
        }
    }

    private fun showCreateAccountDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_account, null)

        val etNewUsername = dialogView.findViewById<EditText>(R.id.etNewUsername)
        val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Create New Account")
            .setView(dialogView)
            .setPositiveButton("Create", null)
            .setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val email = etNewUsername.text.toString().trim()
                val password = etNewPassword.text.toString().trim()
                val confirmPassword = etConfirmPassword.text.toString().trim()

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            } else {
                                Toast.makeText(this, "Account creation failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        dialog.show()
    }
}
