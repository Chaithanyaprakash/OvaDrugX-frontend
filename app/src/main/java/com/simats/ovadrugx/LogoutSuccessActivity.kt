package com.simats.ovadrugx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.widget.Button

class LogoutSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout_success)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            // Clear the back stack so the user cannot go back to the logout screen or app
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
