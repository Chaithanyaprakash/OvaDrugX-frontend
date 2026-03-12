package com.simats.ovadrugx

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.simats.ovadrugx.api.RetrofitClient
import com.simats.ovadrugx.model.ScreenDrugRequest
import com.simats.ovadrugx.model.ScreenDrugResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScreeningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screening)

        setupBottomNavigation()
        setupClickListeners()
        setupScreeningLogic()

        // Phase 1 Automation: Intercept Virtual Docking intent
        val autoGene = intent.getStringExtra("AUTO_SCAN_GENE")
        if (autoGene != null) {
            findViewById<EditText>(R.id.et_screening_gene).setText(autoGene)
            // Trigger the AI scan programmatically
            findViewById<Button>(R.id.btn_run_screening).performClick()
        }
    }
    
    private fun setupClickListeners() {
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

    }

    private fun setupScreeningLogic() {
        val btnRunScreening = findViewById<Button>(R.id.btn_run_screening)
        
        btnRunScreening.setOnClickListener {
            val gene = findViewById<EditText>(R.id.et_screening_gene).text.toString().trim()
            val mirna = findViewById<EditText>(R.id.et_screening_mirna).text.toString().trim()
            val compound = findViewById<EditText>(R.id.et_screening_compound).text.toString().trim()

            if (gene.isEmpty() && mirna.isEmpty() && compound.isEmpty()) {
                Toast.makeText(this, "Please enter at least one biomarker to screen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Provide default placeholders if fields are left blank but at least one exists
            val request = ScreenDrugRequest(
                gene = if (gene.isNotEmpty()) gene else "Unknown",
                mirna = if (mirna.isNotEmpty()) mirna else "Unknown",
                compound = if (compound.isNotEmpty()) compound else "Unknown"
            )

            // Hide previous results and static content while loading
            findViewById<View>(R.id.card_dynamic_screening).visibility = View.GONE
            findViewById<View>(R.id.container_static_results).visibility = View.GONE
            Toast.makeText(this, "Running Multi-Model AI Screening...", Toast.LENGTH_SHORT).show()

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "ovadrugx_screening"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "Screening Process", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
            
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Screening in Progress")
                .setContentText("Analyzing data via AI Models...")
                .setProgress(0, 0, true)
                .setOngoing(true)
            
            notificationManager.notify(2001, notificationBuilder.build())

            RetrofitClient.instance.screenDrug(request).enqueue(object : Callback<ScreenDrugResponse> {
                override fun onResponse(call: Call<ScreenDrugResponse>, response: Response<ScreenDrugResponse>) {
                    val body = response.body()
                    if (response.isSuccessful && body != null && body.status == "success" && body.data != null) {
                        val compoundName = body.data.recommendedDrug ?: "Unknown"
                        
                        val geneStr = findViewById<EditText>(R.id.et_screening_gene).text.toString().trim()
                        val finalGene = if (geneStr.isNotEmpty()) geneStr else "Unknown Gene"
                        
                        val summaryIntent = Intent(this@ScreeningActivity, RiskSummaryActivity::class.java).apply {
                            putExtra("RISK_GENE_NAME", finalGene)
                            putExtra("RISK_DRUG_NAME", compoundName)
                            putExtra("RISK_MATCH_SCORE", body.data.overallMatchScore ?: 0.0)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        
                        val pendingIntent = PendingIntent.getActivity(
                            this@ScreeningActivity,
                            0,
                            summaryIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        
                        notificationBuilder.setContentTitle("Screening Complete")
                            .setContentText("Screening of compound $compoundName completed kindly check that.")
                            .setProgress(0, 0, false)
                            .setOngoing(false)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                        notificationManager.notify(2001, notificationBuilder.build())
                        
                        displayDynamicScreeningResult(body.data)
                    } else {
                        notificationBuilder.setContentTitle("Screening Failed")
                            .setContentText(body?.message ?: "AI Screening Failed")
                            .setProgress(0, 0, false)
                            .setOngoing(false)
                            .setAutoCancel(true)
                        notificationManager.notify(2001, notificationBuilder.build())
                        
                        Toast.makeText(this@ScreeningActivity, body?.message ?: "AI Screening Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ScreenDrugResponse>, t: Throwable) {
                    notificationBuilder.setContentTitle("Screening Error")
                        .setContentText("Network error: ${t.message}")
                        .setProgress(0, 0, false)
                        .setOngoing(false)
                        .setAutoCancel(true)
                    notificationManager.notify(2001, notificationBuilder.build())
                    
                    Toast.makeText(this@ScreeningActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        populateScreeningHistory()
    }

    private fun populateScreeningHistory() {
        val containerHistory = findViewById<android.widget.LinearLayout>(R.id.container_screening_history) ?: return
        containerHistory.removeAllViews()

        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", Context.MODE_PRIVATE)
        val genes = sharedPrefs.getString("HISTORY_GENES", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
        val scores = sharedPrefs.getString("HISTORY_SCORES", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()

        if (genes.isEmpty()) {
            val tvNoData = TextView(this).apply {
                text = "No recent screenings found."
                setTextColor(android.graphics.Color.GRAY)
                textSize = 14f
                setPadding(0, 16, 0, 16)
            }
            containerHistory.addView(tvNoData)
            return
        }

        // Mapping latest to first (they are already stored with latest at 0 index)
        for (i in genes.indices) {
            val geneName = genes[i]
            val scoreStr = scores.getOrNull(i) ?: "0"
            val scoreVal = scoreStr.toDoubleOrNull() ?: 0.0
            val intScore = scoreVal.toInt()

            // Root row container
            val rowLayout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, (24 * resources.displayMetrics.density).toInt()) }
            }

            // Top section (Image + Name + Status dot)
            val topSection = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val textContainer = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvName = TextView(this).apply {
                text = geneName
                setTextColor(android.graphics.Color.BLACK)
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val tvSuitability = TextView(this).apply {
                text = when {
                    intScore >= 80 -> "High therapeutic potential detected"
                    intScore >= 50 -> "Moderate therapeutic potential detected"
                    else -> "Low therapeutic potential detected"
                }
                setTextColor(android.graphics.Color.parseColor("#7DA5B3"))
                textSize = 12f
            }

            textContainer.addView(tvName)
            textContainer.addView(tvSuitability)

            val statusDot = android.widget.ImageView(this).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    (12 * resources.displayMetrics.density).toInt(),
                    (12 * resources.displayMetrics.density).toInt()
                )
                setImageResource(R.drawable.bg_rounded_gradient)
                imageTintList = android.content.res.ColorStateList.valueOf(
                    if (intScore >= 60) android.graphics.Color.parseColor("#4CAF50") else android.graphics.Color.parseColor("#FFC107")
                )
            }

            topSection.addView(textContainer)
            topSection.addView(statusDot)

            // Middle section (Label + Percent)
            val labelSection = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = (12 * resources.displayMetrics.density).toInt() }
            }

            val tvMatchLabel = TextView(this).apply {
                text = "Patient Match Score"
                setTextColor(android.graphics.Color.BLACK)
                textSize = 12f
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvPercent = TextView(this).apply {
                text = "$intScore%"
                setTextColor(android.graphics.Color.BLACK)
                textSize = 12f
            }

            labelSection.addView(tvMatchLabel)
            labelSection.addView(tvPercent)

            // Bottom section (Progress Bar)
            val pb = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    (6 * resources.displayMetrics.density).toInt()
                ).apply { topMargin = (8 * resources.displayMetrics.density).toInt() }
                progress = intScore
                progressDrawable = ContextCompat.getDrawable(this@ScreeningActivity, R.drawable.bg_custom_progress)
                progressTintList = android.content.res.ColorStateList.valueOf(
                    when {
                        intScore >= 80 -> android.graphics.Color.parseColor("#4CAF50")
                        intScore >= 50 -> android.graphics.Color.parseColor("#FFC107")
                        else -> android.graphics.Color.parseColor("#F44336")
                    }
                )
            }

            rowLayout.addView(topSection)
            rowLayout.addView(labelSection)
            rowLayout.addView(pb)

            containerHistory.addView(rowLayout)
        }
    }

    private fun displayDynamicScreeningResult(data: com.simats.ovadrugx.model.ScreenDrugData) {
        val dynamicCard = findViewById<View>(R.id.card_dynamic_screening)
        val tvDrugName = findViewById<TextView>(R.id.tv_dynamic_drug_name)
        val tvDrugDesc = findViewById<TextView>(R.id.tv_dynamic_drug_desc)
        val tvMatchPercent = findViewById<TextView>(R.id.tv_dynamic_match_percent)
        val progressBar = findViewById<ProgressBar>(R.id.progress_dynamic_match)

        // Ensure 0-100 bounding
        var matchScore = data.overallMatchScore ?: 0.0
        matchScore = Math.max(0.0, Math.min(100.0, matchScore))
        val intScore = matchScore.toInt()

        tvDrugName.text = data.recommendedDrug ?: "Synthetic Compound"
        tvDrugDesc.text = data.suitabilityRating ?: "Analyzed via Random Forest, SVM, and GNN Models."
        tvMatchPercent.text = "${intScore}%"
        
        // Android ProgressBar expects integers
        progressBar.progress = intScore

        // Color coding the progress bar based on the AI's confidence
        val colorRet = when {
            intScore >= 80 -> android.graphics.Color.parseColor("#4CAF50") // Green
            intScore >= 50 -> android.graphics.Color.parseColor("#FFC107") // Yellow
            else -> android.graphics.Color.parseColor("#F44336") // Red
        }
        progressBar.progressTintList = android.content.res.ColorStateList.valueOf(colorRet)

        dynamicCard.visibility = View.VISIBLE

        // Refreshes the recent screenings list automatically
        populateScreeningHistory()

        // Phase 8: Commit Drug Screening Math to Global Memory Graph History
        val geneStr = findViewById<EditText>(R.id.et_screening_gene).text.toString().trim()
        val finalGene = if (geneStr.isNotEmpty()) geneStr else "Unknown Gene"

        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", android.content.Context.MODE_PRIVATE)
        val currentGenes = sharedPrefs.getString("HISTORY_GENES", "") ?: ""
        val currentScores = sharedPrefs.getString("HISTORY_SCORES", "") ?: ""
        val currentDates = sharedPrefs.getString("HISTORY_DATES", "") ?: ""
        val currentDrugs = sharedPrefs.getString("HISTORY_DRUGS", "") ?: ""
        
        val newDate = System.currentTimeMillis().toString()
        val newDrug = data.recommendedDrug ?: "Unknown Compound"
        val geneList = currentGenes.split(",").filter { it.isNotEmpty() }.toMutableList()
        val scoreList = currentScores.split(",").filter { it.isNotEmpty() }.toMutableList()
        val dateList = currentDates.split(",").filter { it.isNotEmpty() }.toMutableList()
        val drugList = currentDrugs.split(",").filter { it.isNotEmpty() }.toMutableList()
        
        val existingIndex = geneList.indexOf(finalGene)
        if (existingIndex != -1) {
            val existingScore = scoreList.getOrNull(existingIndex)?.toFloatOrNull() ?: 0f
            if (matchScore > existingScore) {
                geneList.removeAt(existingIndex)
                if (existingIndex < scoreList.size) scoreList.removeAt(existingIndex)
                if (existingIndex < dateList.size) dateList.removeAt(existingIndex)
                if (existingIndex < drugList.size) drugList.removeAt(existingIndex)

                geneList.add(0, finalGene)
                scoreList.add(0, matchScore.toString())
                dateList.add(0, newDate)
                drugList.add(0, newDrug)
            }
        } else {
            geneList.add(0, finalGene)
            scoreList.add(0, matchScore.toString())
            dateList.add(0, newDate)
            drugList.add(0, newDrug)
        }
        
        while (geneList.size > 5) {
            geneList.removeAt(geneList.size - 1)
            if (scoreList.size > 5) scoreList.removeAt(scoreList.size - 1)
            if (dateList.size > 5) dateList.removeAt(dateList.size - 1)
            if (drugList.size > 5) drugList.removeAt(drugList.size - 1)
        }
        
        sharedPrefs.edit().apply {
            putString("HISTORY_GENES", geneList.joinToString(","))
            putString("HISTORY_SCORES", scoreList.joinToString(","))
            putString("HISTORY_DATES", dateList.joinToString(","))
            putString("HISTORY_DRUGS", drugList.joinToString(","))
            putString("LATEST_COMPOUND", data.recommendedDrug ?: "Unknown Compound")
            apply()
        }

        // Refresh list ONE MORE TIME after commit
        populateScreeningHistory()

        // Automatically Redirect to Risk Summary if Highest Compatibility (>80%)
        if (intScore >= 80) {
            val intent = Intent(this, RiskSummaryActivity::class.java).apply {
                putExtra("RISK_GENE_NAME", finalGene)
                putExtra("RISK_DRUG_NAME", data.recommendedDrug)
                putExtra("RISK_MATCH_SCORE", data.overallMatchScore ?: 0.0)
            }
            Toast.makeText(this, "Highest Compatibility Found! Redirecting to Risk Summary...", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
    }
    
    private fun setupBottomNavigation() {
        findViewById<View>(R.id.nav_home).setOnClickListener {
             startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<View>(R.id.nav_screening).setOnClickListener {
             // Already on Screening
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
