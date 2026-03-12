package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        setupBottomNavigation()
        setupMenuActions()

        // Load User Data
        val sharedPreferences = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val fullName = sharedPreferences.getString("full_name", "User")
        
        val tvName = findViewById<android.widget.TextView>(R.id.tv_name)
        tvName.text = fullName

        val imgProfile = findViewById<android.widget.ImageView>(R.id.img_profile)
        if (isFemaleName(fullName ?: "")) {
            imgProfile.setImageResource(R.drawable.ic_profile_female)
        } else {
            imgProfile.setImageResource(R.drawable.ic_profile_male)
        }

        findViewById<View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }
    }

    private fun isFemaleName(fullName: String): Boolean {
        val name = fullName.lowercase()
        if (name.contains("mrs.") || name.contains("ms.") || name.contains("miss")) return true
        if (name.contains("mr.")) return false

        val words = name.replace("dr.", "").trim().split(" ")
        val firstName = words.firstOrNull() ?: return false
        
        val femaleEndings = listOf("a", "i", "ee", "ia", "ya", "na", "ta")
        val maleExceptions = listOf("aditya", "krishna", "shiva", "datta", "chandra", "surya", "chaitanya")

        if (maleExceptions.contains(firstName)) return false
        return femaleEndings.any { firstName.endsWith(it) }
    }

    private fun setupMenuActions() {
        // Account Information
        findViewById<View>(R.id.btn_account_info).setOnClickListener {
            startActivity(Intent(this, AccountInformationActivity::class.java))
        }

        // Security & Biometrics
        findViewById<View>(R.id.btn_security).setOnClickListener {
            startActivity(Intent(this, SecurityActivity::class.java))
        }

        // Privacy Policy
        findViewById<View>(R.id.btn_privacy).setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }

        // Help & Support
        findViewById<View>(R.id.btn_help).setOnClickListener {
            startActivity(Intent(this, HelpSupportActivity::class.java))
        }

        // Log Out
        findViewById<View>(R.id.btn_logout).setOnClickListener {
             val intent = Intent(this, LoginActivity::class.java)
             intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
             startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.nav_home).setOnClickListener {
             startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<View>(R.id.nav_screening).setOnClickListener {
             startActivity(Intent(this, ScreeningActivity::class.java))
        }
        findViewById<View>(R.id.nav_targets).setOnClickListener {
             startActivity(Intent(this, TargetDiscoveryActivity::class.java))
        }
        findViewById<View>(R.id.nav_insights).setOnClickListener {
             startActivity(Intent(this, AiInsightsActivity::class.java))
        }
        findViewById<View>(R.id.nav_profile).setOnClickListener {
             // Already on Profile
        }
    }
}
