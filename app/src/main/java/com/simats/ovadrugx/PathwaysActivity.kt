package com.simats.ovadrugx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent

class PathwaysActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pathways)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        updateWithLatestScreening()
    }

    private fun updateWithLatestScreening() {
        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", android.content.Context.MODE_PRIVATE)
        val genes = sharedPrefs.getString("HISTORY_GENES", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
        val drugs = sharedPrefs.getString("HISTORY_DRUGS", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
        val scores = sharedPrefs.getString("HISTORY_SCORES", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()

        if (genes.isNotEmpty() && scores.isNotEmpty()) {
            val latestGene = genes[0]
            val latestDrug = drugs.getOrNull(0) ?: "Unknown Compound"
            val latestScore = scores[0].toFloatOrNull() ?: 0f

            val tvDrug = findViewById<android.widget.TextView>(R.id.tv_pathway_drug_1)
            val tvComponents = findViewById<android.widget.TextView>(R.id.tv_pathway_components_1)
            val tvCompat = findViewById<android.widget.TextView>(R.id.tv_pathway_compat_1)
            val pbCompat = findViewById<android.widget.ProgressBar>(R.id.pb_pathway_compat_1)
            val tvTox = findViewById<android.widget.TextView>(R.id.tv_pathway_tox_1)
            val pbTox = findViewById<android.widget.ProgressBar>(R.id.pb_pathway_tox_1)

            if (tvDrug != null) {
                tvDrug.text = "$latestDrug (AI Target Recommendation)"
                tvComponents.text = "Screening Target: $latestGene"
                
                val compatInt = latestScore.toInt()
                tvCompat.text = "Compatibility: $compatInt%"
                pbCompat.progress = compatInt
                
                val toxInt = ((100f - latestScore) * 0.4f).toInt().coerceIn(0, 100)
                tvTox.text = "Toxicity: $toxInt%"
                pbTox.progress = toxInt
            }
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
