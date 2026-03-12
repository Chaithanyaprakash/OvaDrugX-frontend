package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        // Handle Back Button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Handle Update Password
        findViewById<AppCompatButton>(R.id.btnUpdatePassword).setOnClickListener {
             // Validate and mock success
             startActivity(Intent(this, PasswordChangedActivity::class.java))
             finish()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        findViewById<android.view.View>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<android.view.View>(R.id.nav_screening).setOnClickListener {
            startActivity(Intent(this, ScreeningActivity::class.java))
        }
        findViewById<android.view.View>(R.id.nav_targets).setOnClickListener {
            startActivity(Intent(this, TargetDiscoveryActivity::class.java))
        }
        findViewById<android.view.View>(R.id.nav_insights).setOnClickListener {
            startActivity(Intent(this, AiInsightsActivity::class.java))
        }
        findViewById<android.view.View>(R.id.nav_profile).setOnClickListener {
            // Already on a profile sub-page
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
