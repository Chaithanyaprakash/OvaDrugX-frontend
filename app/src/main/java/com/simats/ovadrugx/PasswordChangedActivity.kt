package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class PasswordChangedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_changed)

        // Handle Back to Login
        findViewById<AppCompatButton>(R.id.btnBackToLogin).setOnClickListener {
             val intent = Intent(this, LoginActivity::class.java)
             intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
             startActivity(intent)
        }
    }
}
