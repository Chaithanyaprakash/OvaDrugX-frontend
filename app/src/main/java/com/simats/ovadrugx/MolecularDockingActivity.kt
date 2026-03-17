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
        loadLatestScreeningData()
    }

    private fun loadLatestScreeningData() {
        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", android.content.Context.MODE_PRIVATE)
        val genes = sharedPrefs.getString("HISTORY_GENES", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
        val scores = sharedPrefs.getString("HISTORY_SCORES", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
        val drugs = sharedPrefs.getString("HISTORY_DRUGS", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()

        if (genes.isNotEmpty()) {
            val latestGene = genes[0]
            val latestScore = scores.getOrNull(0)?.toDoubleOrNull() ?: 0.0
            val latestDrug = drugs.getOrNull(0) ?: "Recommended Compound"

            // Update Metrics
            // Simulated binding affinity: - (score / 10)
            val affinity = -(latestScore / 10.0)
            findViewById<android.widget.TextView>(R.id.tv_metrics_affinity)?.text = String.format("%.1f kcal/mol", affinity)
            findViewById<android.widget.TextView>(R.id.tv_metrics_confidence)?.text = "${latestScore.toInt()}%"

            // Update Target Details Header
            findViewById<android.widget.TextView>(R.id.tv_target_details_title)?.text = "Target Details: $latestGene"

            // Update Top Candidates
            findViewById<android.widget.TextView>(R.id.tv_candidate_1_name)?.text = latestDrug
            findViewById<android.widget.TextView>(R.id.tv_candidate_1_score)?.text = "Match Score: ${latestScore.toInt()}%"
        }
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
