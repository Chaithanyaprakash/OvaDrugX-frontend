package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MolecularDockingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_molecular_docking)
        
        setupBottomNavigation()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }
        
        // Candidates clicks (can link to details or screening item)
         findViewById<View>(R.id.card_candidate_1).setOnClickListener {
             // Placeholder action
        }
         findViewById<View>(R.id.card_candidate_2).setOnClickListener {
             // Placeholder action
        }
         findViewById<View>(R.id.card_candidate_3).setOnClickListener {
             // Placeholder action
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
             startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
