package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ItemScreeningPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_screening_page)

        // Check for both keys to be safe
        val drugName = intent.getStringExtra("COMPOUND_NAME") ?: intent.getStringExtra("DRUG_NAME") ?: "Compound"
        val score = intent.getStringExtra("MATCH_SCORE") ?: "N/A"
        val potential = intent.getStringExtra("POTENTIAL") ?: "Unknown"

        findViewById<TextView>(R.id.tvDrugName).text = drugName
        findViewById<TextView>(R.id.tvMatchScore).text = "Match Score: $score"
        findViewById<TextView>(R.id.tvDescription).text = "$drugName shows potential for current biomarker profile. Further analysis is recommended in the Risk Effectiveness module."

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<android.view.View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        findViewById<android.view.View>(R.id.btn_risk_effectiveness).setOnClickListener {
             startActivity(Intent(this, RiskEffectivenessActivity::class.java))
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
             startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
