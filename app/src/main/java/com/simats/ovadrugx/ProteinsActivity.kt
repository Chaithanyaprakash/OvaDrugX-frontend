package com.simats.ovadrugx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent

class ProteinsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proteins)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        setupBottomNavigation()
        loadScreenedProteins()
    }

    private fun loadScreenedProteins() {
        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", android.content.Context.MODE_PRIVATE)
        val genes = sharedPrefs.getString("HISTORY_GENES", "") ?: ""
        val scores = sharedPrefs.getString("HISTORY_SCORES", "") ?: ""
        val dates = sharedPrefs.getString("HISTORY_DATES", "") ?: ""

        if (genes.isEmpty()) return

        val geneList = genes.split(",").filter { it.isNotEmpty() }
        val scoreList = scores.split(",").filter { it.isNotEmpty() }
        val dateList = dates.split(",").filter { it.isNotEmpty() }

        val container = findViewById<android.widget.LinearLayout>(R.id.container_screened_proteins)
        container.visibility = View.VISIBLE

        for (i in geneList.indices) {
            val itemView = layoutInflater.inflate(R.layout.item_screened_protein, container, false)
            
            val tvName = itemView.findViewById<android.widget.TextView>(R.id.tv_protein_name)
            val tvDate = itemView.findViewById<android.widget.TextView>(R.id.tv_screening_date)
            val tvScore = itemView.findViewById<android.widget.TextView>(R.id.tv_compatibility_score)

            tvName.text = geneList[i]
            
            val scoreVal = scoreList.getOrNull(i)?.toDoubleOrNull() ?: 0.0
            tvScore.text = "${scoreVal.toInt()}%"

            val timestamp = dateList.getOrNull(i)?.toLongOrNull() ?: 0L
            tvDate.text = if (timestamp > 0) getRelativeTime(timestamp) else "Recently"

            container.addView(itemView)
        }
    }

    private fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000} mins ago"
            diff < 86400000 -> "${diff / 3600000} hours ago"
            else -> "${diff / 86400000} days ago"
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
