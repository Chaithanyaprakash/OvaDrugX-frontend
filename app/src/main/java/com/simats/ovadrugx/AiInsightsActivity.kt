package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AiInsightsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_insights)
        
        setupBottomNavigation()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        
        // Extract Global Array and calculate the Highest Peak match
        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", android.content.Context.MODE_PRIVATE)
        val currentGenes = sharedPrefs.getString("HISTORY_GENES", "") ?: ""
        val currentScores = sharedPrefs.getString("HISTORY_SCORES", "") ?: ""
        
        val geneList = currentGenes.split(",").filter { it.isNotEmpty() }
        val scoreList = currentScores.split(",").filter { it.isNotEmpty() }
        
        var maxScore = 0f
        var maxGene = "No Data"
        
        if (geneList.isNotEmpty() && scoreList.isNotEmpty()) {
            for (i in 0 until Math.min(geneList.size, scoreList.size)) {
                val score = scoreList[i].toFloatOrNull() ?: 0f
                if (score > maxScore) {
                    maxScore = score
                    maxGene = geneList[i]
                }
            }
        }
        
        val progressCircle = findViewById<android.widget.ProgressBar>(R.id.progress_confidence_circle)
        val tvScore = findViewById<android.widget.TextView>(R.id.tv_confidence_score_circle)
        val tvTarget = findViewById<android.widget.TextView>(R.id.tv_confidence_target)

        if (progressCircle != null && tvScore != null && tvTarget != null) {
            progressCircle.progress = maxScore.toInt()
            tvScore.text = String.format("%.1f%%", maxScore)
            tvTarget.text = maxGene
            
            // Adjust Stroke Color Dynamically
            val colorRet = when {
                maxScore >= 80 -> android.graphics.Color.parseColor("#4CAF50") // Green
                maxScore >= 50 -> android.graphics.Color.parseColor("#FFC107") // Yellow
                else -> android.graphics.Color.parseColor("#F44336") // Red
            }
            progressCircle.progressTintList = android.content.res.ColorStateList.valueOf(colorRet)
        }
    }


    private fun setupClickListeners() {
        val btnBack = findViewById<View>(R.id.btn_back)
        btnBack?.setOnClickListener { finish() }

        val btnNotifications = findViewById<View>(R.id.btn_notifications)
        btnNotifications?.setOnClickListener { startActivity(Intent(this, NotificationsActivity::class.java)) }

        val btnUploadReport = findViewById<View>(R.id.btn_upload_report)
        btnUploadReport?.setOnClickListener { startActivity(Intent(this, UploadReportActivity::class.java)) }
        
        val cardReportUpload = findViewById<View>(R.id.card_report_upload)
        cardReportUpload?.setOnClickListener { startActivity(Intent(this, UploadReportActivity::class.java)) }

        val btnViewRisk = findViewById<View>(R.id.btn_view_risk)
        btnViewRisk?.setOnClickListener { startActivity(Intent(this, RiskEffectivenessActivity::class.java)) }
        
        val cardRisk = findViewById<View>(R.id.card_risk_effectiveness)
        cardRisk?.setOnClickListener { startActivity(Intent(this, RiskEffectivenessActivity::class.java)) }

        val btnViewKnowledge = findViewById<View>(R.id.btn_view_knowledge)
        btnViewKnowledge?.setOnClickListener {
            startActivity(Intent(this, KnowledgePathwayActivity::class.java))
        }

        val cardKnowledgePathway = findViewById<View>(R.id.card_knowledge_pathway)
        cardKnowledgePathway?.setOnClickListener {
            startActivity(Intent(this, KnowledgePathwayActivity::class.java))
        }
    }

    fun goToKnowledgePathway(view: View) {
        startActivity(Intent(this, KnowledgePathwayActivity::class.java))
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
             // Already on Insights
        }
        findViewById<View>(R.id.nav_profile).setOnClickListener {
             startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
