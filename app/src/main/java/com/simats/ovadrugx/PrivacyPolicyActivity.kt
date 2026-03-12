package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class PrivacyPolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        // Handle Back Button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Bottom Navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navScreening).setOnClickListener {
            startActivity(Intent(this, ScreeningActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navTargets).setOnClickListener {
            startActivity(Intent(this, TargetDiscoveryActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navInsights).setOnClickListener {
            startActivity(Intent(this, AiInsightsActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener {
            // Return to main Profile page or just finish
            finish() 
        }
    }
}
