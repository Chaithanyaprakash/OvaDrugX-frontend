package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AiDiscoveriesActivity : AppCompatActivity() {
    private data class ScreeningRecord(
        val timestamp: Long,
        val confidence: Float,
        val target: String
    )

    private val allRecords = mutableListOf<ScreeningRecord>()
    private var currentSortMode = "DATE_DESC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_discoveries)
        
        // Handle back button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<android.view.View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        
        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", android.content.Context.MODE_PRIVATE)
        val currentGenes = sharedPrefs.getString("HISTORY_GENES", "") ?: ""
        val currentScores = sharedPrefs.getString("HISTORY_SCORES", "") ?: ""
        val currentDates = sharedPrefs.getString("HISTORY_DATES", "") ?: ""
        
        val geneList = currentGenes.split(",").filter { it.isNotEmpty() }
        val scoreList = currentScores.split(",").filter { it.isNotEmpty() }
        val dateList = currentDates.split(",").filter { it.isNotEmpty() }

        allRecords.clear()
        val baseTime = System.currentTimeMillis()

        for (i in 0 until Math.min(geneList.size, scoreList.size)) {
            val score = scoreList[i].toFloatOrNull() ?: 0f
            val gene = geneList[i]
            
            val time = if (i < dateList.size) {
                dateList[i].toLongOrNull() ?: (baseTime - (i * 3600000L))
            } else {
                baseTime - (i * 3600000L) 
            }
            
            allRecords.add(ScreeningRecord(time, score, gene))
        }

        setupSortListeners()
        applySortAndRender()
    }

    private fun setupSortListeners() {
        val headerDate = findViewById<android.widget.TextView>(R.id.header_sort_date)
        val headerConf = findViewById<android.widget.TextView>(R.id.header_sort_confidence)
        val headerTarget = findViewById<android.widget.TextView>(R.id.header_sort_target)

        headerDate?.setOnClickListener {
            currentSortMode = if (currentSortMode == "DATE_DESC") "DATE_ASC" else "DATE_DESC"
            updateHeaderUI(headerDate, headerConf, headerTarget, currentSortMode)
            applySortAndRender()
        }

        headerConf?.setOnClickListener {
            currentSortMode = if (currentSortMode == "CONF_DESC") "CONF_ASC" else "CONF_DESC"
            updateHeaderUI(headerDate, headerConf, headerTarget, currentSortMode)
            applySortAndRender()
        }

        headerTarget?.setOnClickListener {
            currentSortMode = if (currentSortMode == "TARGET_ASC") "TARGET_DESC" else "TARGET_ASC"
            updateHeaderUI(headerDate, headerConf, headerTarget, currentSortMode)
            applySortAndRender()
        }
    }

    private fun updateHeaderUI(hDate: android.widget.TextView?, hConf: android.widget.TextView?, hTarget: android.widget.TextView?, mode: String) {
        val inactiveColor = android.graphics.Color.parseColor("#757575")
        val activeColor = android.graphics.Color.parseColor("#1976D2")

        hDate?.text = "Date"
        hDate?.setTextColor(inactiveColor)
        hConf?.text = "Confidence"
        hConf?.setTextColor(inactiveColor)
        hTarget?.text = "Target"
        hTarget?.setTextColor(inactiveColor)

        when (mode) {
            "DATE_DESC" -> { hDate?.text = "Date ▼"; hDate?.setTextColor(activeColor) }
            "DATE_ASC" -> { hDate?.text = "Date ▲"; hDate?.setTextColor(activeColor) }
            "CONF_DESC" -> { hConf?.text = "Confidence ▼"; hConf?.setTextColor(activeColor) }
            "CONF_ASC" -> { hConf?.text = "Confidence ▲"; hConf?.setTextColor(activeColor) }
            "TARGET_ASC" -> { hTarget?.text = "Target ▲"; hTarget?.setTextColor(activeColor) }
            "TARGET_DESC" -> { hTarget?.text = "Target ▼"; hTarget?.setTextColor(activeColor) }
        }
    }

    private fun getRelativeTimeSpanString(time: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - time
        val days = diff / (1000 * 60 * 60 * 24)
        val hours = diff / (1000 * 60 * 60)
        val minutes = diff / (1000 * 60)
        
        return when {
            days > 1 -> "$days days ago"
            days == 1L -> "1 day ago"
            hours > 1 -> "$hours hours ago"
            hours == 1L -> "1 hour ago"
            minutes > 1 -> "$minutes mins ago"
            minutes == 1L -> "1 min ago"
            else -> "Just now"
        }
    }

    private fun applySortAndRender() {
        val sortedList = when (currentSortMode) {
            "DATE_DESC" -> allRecords.sortedByDescending { it.timestamp }
            "DATE_ASC" -> allRecords.sortedBy { it.timestamp }
            "CONF_DESC" -> allRecords.sortedByDescending { it.confidence }
            "CONF_ASC" -> allRecords.sortedBy { it.confidence }
            "TARGET_ASC" -> allRecords.sortedByDescending { it.timestamp }
            "TARGET_DESC" -> allRecords.sortedBy { it.timestamp }
            else -> allRecords
        }

        val container = findViewById<android.widget.LinearLayout>(R.id.container_ai_discoveries)
        container?.removeAllViews()

        for (record in sortedList) {
            val row = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, (12 * resources.displayMetrics.density).toInt(), 0, (12 * resources.displayMetrics.density).toInt())
                isBaselineAligned = false
            }

            val tvDate = android.widget.TextView(this).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = getRelativeTimeSpanString(record.timestamp)
                setTextColor(android.graphics.Color.BLACK)
                textSize = 14f
            }

            val tvConf = android.widget.TextView(this).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "${record.confidence.toInt()}%"
                setTextColor(android.graphics.Color.BLACK)
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val tvTarget = android.widget.TextView(this).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = record.target
                setTextColor(android.graphics.Color.parseColor("#1976D2"))
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            row.addView(tvDate)
            row.addView(tvConf)
            row.addView(tvTarget)

            container?.addView(row)
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
