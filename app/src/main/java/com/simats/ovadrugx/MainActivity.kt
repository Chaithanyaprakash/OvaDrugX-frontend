package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check for session/auth here if needed, else go to HomeActivity
        
        // For now, immediately redirect to HomeActivity
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Close MainActivity so it's removed from back stack
    }
}