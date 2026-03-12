package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class RiskSummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_summary)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        // Phase 2: Retrieve the drug matching metrics from the automated AI sequence!
        val gene = intent.getStringExtra("RISK_GENE_NAME") ?: "Target Molecule"
        val drug = intent.getStringExtra("RISK_DRUG_NAME") ?: "Novel Compound"
        val score = intent.getDoubleExtra("RISK_MATCH_SCORE", 0.0)

        findViewById<android.widget.TextView>(R.id.tv_risk_target).text = gene
        findViewById<android.widget.TextView>(R.id.tv_risk_drug).text = drug
        findViewById<android.widget.TextView>(R.id.tv_risk_score).text = "${score.toInt()}%"

        // Phase 3: Route the Export button to the dedicated Report Export Center
        findViewById<View>(R.id.btn_export_pdf).setOnClickListener {
            val exportIntent = Intent(this, ReportExportActivity::class.java).apply {
                putExtra("RISK_GENE_NAME", gene)
                putExtra("RISK_DRUG_NAME", drug)
                putExtra("RISK_MATCH_SCORE", score)
            }
            startActivity(exportIntent)
        }

        setupBottomNavigation()
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
