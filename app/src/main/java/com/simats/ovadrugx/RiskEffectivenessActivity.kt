package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class RiskEffectivenessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_effectiveness)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        findViewById<View>(R.id.card_risk_summary).setOnClickListener {
            startActivity(Intent(this, RiskSummaryActivity::class.java))
        }

        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        // Phase 4 Engine Extension: Redraw the Weekly Charts based on Global AI State!
        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", android.content.Context.MODE_PRIVATE)
        val latestGene = sharedPrefs.getString("LATEST_GENE", null)
        val latestConfidence = sharedPrefs.getFloat("LATEST_CONFIDENCE", -1f)
        val latestCompound = sharedPrefs.getString("LATEST_COMPOUND", "Unknown Compound")

        if (latestGene != null && latestConfidence != -1f) {
            val tvWeeklyTitle = findViewById<android.widget.TextView>(R.id.tv_weekly_report_title)
            val tvPredictedEfficacy = findViewById<android.widget.TextView>(R.id.tv_predicted_efficacy)
            val tvSafetyProfile = findViewById<android.widget.TextView>(R.id.tv_safety_profile)

            if (tvWeeklyTitle != null) {
                tvWeeklyTitle.text = "Weekly Report: $latestCompound"
            }

            if (tvPredictedEfficacy != null) {
                tvPredictedEfficacy.text = "${latestConfidence.toInt()}%"
                if (latestConfidence >= 80) {
                    tvPredictedEfficacy.setTextColor(android.graphics.Color.parseColor("#4CAF50")) // Green
                } else if (latestConfidence >= 50) {
                    tvPredictedEfficacy.setTextColor(android.graphics.Color.parseColor("#FFC107")) // Yellow
                } else {
                    tvPredictedEfficacy.setTextColor(android.graphics.Color.parseColor("#F44336")) // Red
                }
            }
            
            // Safety Profile math based on target efficacy
            val safetyScore = (latestConfidence - 2f).coerceIn(0f, 100f)
            tvSafetyProfile?.text = "${safetyScore.toInt()}%"
            
            // Toxicity inversely proportional distribution based on AI confidence deviation
            val cardioPercent = ((100f - latestConfidence) * 0.4f).coerceIn(0f, 100f)
            val hepatoPercent = ((100f - latestConfidence) * 0.35f).coerceIn(0f, 100f)
            val neuroPercent = ((100f - latestConfidence) * 0.25f).coerceIn(0f, 100f)

            findViewById<android.widget.TextView>(R.id.tv_cardio_percent)?.text = String.format("%.1f%%", cardioPercent)
            findViewById<android.view.View>(R.id.bar_cardio)?.let { view ->
                val params = view.layoutParams
                params.height = (cardioPercent * 2 * resources.displayMetrics.density).toInt().coerceAtLeast(1)
                view.layoutParams = params
            }
            
            findViewById<android.widget.TextView>(R.id.tv_hepato_percent)?.text = String.format("%.1f%%", hepatoPercent)
            findViewById<android.view.View>(R.id.bar_hepato)?.let { view ->
                val params = view.layoutParams
                params.height = (hepatoPercent * 2 * resources.displayMetrics.density).toInt().coerceAtLeast(1)
                view.layoutParams = params
            }
            
            findViewById<android.widget.TextView>(R.id.tv_neuro_percent)?.text = String.format("%.1f%%", neuroPercent)
            findViewById<android.view.View>(R.id.bar_neuro)?.let { view ->
                val params = view.layoutParams
                params.height = (neuroPercent * 2 * resources.displayMetrics.density).toInt().coerceAtLeast(1)
                view.layoutParams = params
            }

            // Draw Weekly Progress Sequence
            val weeklyContainer = findViewById<android.widget.LinearLayout>(R.id.container_weekly_graph)
            if (weeklyContainer != null) {
                weeklyContainer.removeAllViews()
                val scores = listOf(
                    (latestConfidence * 0.7f).coerceIn(0f, 100f),
                    (latestConfidence * 0.82f).coerceIn(0f, 100f),
                    (latestConfidence * 0.91f).coerceIn(0f, 100f),
                    latestConfidence
                )
                for (i in 0 until 4) {
                    val score = scores[i]
                    val weekLabel = "Week ${i + 1}"
                    
                    val barLayout = android.widget.LinearLayout(this).apply {
                        orientation = android.widget.LinearLayout.VERTICAL
                        gravity = android.view.Gravity.CENTER_HORIZONTAL or android.view.Gravity.BOTTOM
                        layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    }
                    
                    val tvScore = android.widget.TextView(this).apply {
                        text = "${score.toInt()}%"
                        setTextColor(android.graphics.Color.parseColor("#333333"))
                        textSize = 10f
                        setTypeface(null, android.graphics.Typeface.BOLD)
                        setPadding(0, 0, 0, (4 * resources.displayMetrics.density).toInt())
                    }
                    
                    val barView = android.view.View(this).apply {
                        setBackgroundColor(android.graphics.Color.parseColor(if (i == 3) "#4CAF50" else "#2196F3"))
                        layoutParams = android.widget.LinearLayout.LayoutParams(
                            (30 * resources.displayMetrics.density).toInt(),
                            ((score * 1.0).toInt() * resources.displayMetrics.density).toInt().coerceAtLeast(1)
                        )
                    }
                    
                    val tvWeek = android.widget.TextView(this).apply {
                        text = weekLabel
                        setTextColor(android.graphics.Color.parseColor("#555555"))
                        textSize = 10f
                        setPadding(0, (4 * resources.displayMetrics.density).toInt(), 0, 0)
                    }
                    
                    barLayout.addView(tvScore)
                    barLayout.addView(barView)
                    barLayout.addView(tvWeek)
                    weeklyContainer.addView(barLayout)
                }
            }
        }
    }

    private fun setupNavigation() {
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
