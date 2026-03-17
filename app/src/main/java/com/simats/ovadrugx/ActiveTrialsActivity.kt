package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ActiveTrialsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_trials)
        
        // Handle back button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<android.view.View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        setupBottomNavigation()
        loadLatestScreeningForTrial()
    }

    private fun loadLatestScreeningForTrial() {
        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", android.content.Context.MODE_PRIVATE)
        val latestGene = sharedPrefs.getString("LAST_SCREENED_GENE", null)
        val latestScore = sharedPrefs.getFloat("LAST_SCREENED_SCORE", -1f)
        val latestDrug = sharedPrefs.getString("LAST_SCREENED_DRUG", null)

        if (latestGene != null && latestScore != -1f) {
            findViewById<android.widget.TextView>(R.id.tv_trial_1_name)?.text = "Trial: $latestGene"
            findViewById<android.widget.TextView>(R.id.tv_trial_1_type)?.text = "Recommended: $latestDrug"
            
            val progress = latestScore.toInt().coerceIn(0, 100)
            findViewById<android.widget.ProgressBar>(R.id.pb_trial_1_progress)?.progress = progress
            findViewById<android.widget.TextView>(R.id.tv_trial_1_progress_text)?.text = "$progress%"
            
            findViewById<android.widget.TextView>(R.id.tv_trial_1_status)?.apply {
                text = "Target: Active"
                setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            }
        }
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
