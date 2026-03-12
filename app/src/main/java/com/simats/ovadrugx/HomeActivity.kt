package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simats.ovadrugx.api.AiDataService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupNavigation()
        setupDashboardInteractions()
        
        // Start the background data simulation
        AiDataService.startLiveSimulation()
        observeAiData()
    }

    override fun onResume() {
        super.onResume()
        // Phase 4 Engine: Redraw the Chart based on Global AI State!
        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", android.content.Context.MODE_PRIVATE)
        
        // Read the History arrays dynamically
        val currentGenes = sharedPrefs.getString("HISTORY_GENES", "") ?: ""
        val currentScores = sharedPrefs.getString("HISTORY_SCORES", "") ?: ""
        
        var geneList = currentGenes.split(",").filter { it.isNotEmpty() }
        var scoreList = currentScores.split(",").filter { it.isNotEmpty() }
        
        if (geneList.isEmpty()) {
            // Unconnected Fallback Constants
            geneList = listOf("TP53", "BRCA1", "PARP1")
            scoreList = listOf("88.5", "76.2", "64.0")
        }
        
        val graphContainer = findViewById<android.widget.LinearLayout>(R.id.graph_history_container)
        
        if (graphContainer != null && geneList.isNotEmpty()) {
            graphContainer.removeAllViews() // Clear layout to render fresh graphs
            
            // Apply unique array coloring so graphs are easily distinguishable
            val colors = arrayOf("#FFD700", "#87CEEB", "#00796B", "#8BC34A", "#E040FB")
            
            for (i in 0 until Math.min(geneList.size, scoreList.size)) {
                val gene = geneList[i]
                val score = scoreList[i].toFloatOrNull() ?: 0f
                val colorHex = colors[i % colors.size]
                
                // Generate layout container dynamically for each Bar
                val barLayout = android.widget.LinearLayout(this).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    gravity = android.view.Gravity.CENTER_HORIZONTAL or android.view.Gravity.BOTTOM
                    val params = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.marginEnd = (24 * resources.displayMetrics.density).toInt()
                    layoutParams = params
                }
                
                // Construct top precision score
                val tvScore = android.widget.TextView(this).apply {
                    text = String.format("%.1f%%", score)
                    setTextColor(android.graphics.Color.parseColor("#333333"))
                    textSize = 12f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(0, 0, 0, (8 * resources.displayMetrics.density).toInt())
                }
                
                // Construct the graphical column height
                val barView = android.view.View(this).apply {
                    setBackgroundColor(android.graphics.Color.parseColor(colorHex))
                    val heightPx = (score * 1.2).toInt() * resources.displayMetrics.density.toInt()
                    val finalHeight = if (heightPx > 0) heightPx else (1 * resources.displayMetrics.density).toInt()
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        (50 * resources.displayMetrics.density).toInt(),
                        finalHeight
                    )
                }
                
                // Construct base gene label
                val tvGene = android.widget.TextView(this).apply {
                    text = gene
                    setTextColor(android.graphics.Color.parseColor("#1976D2"))
                    textSize = 10f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
                }
                
                // Attach sequentially (Top to Bottom)
                barLayout.addView(tvScore)
                barLayout.addView(barView)
                barLayout.addView(tvGene)
                
                // Add Graph to horizontal container (Left to Right)
                graphContainer.addView(barLayout)
            }
        }
    }

    private fun observeAiData() {
        val aiDiscoveriesText = findViewById<android.view.ViewGroup>(R.id.chip_ai_discoveries)?.getChildAt(0) as? TextView
        val modelAccuracyText = findViewById<android.view.ViewGroup>(R.id.chip_model_accuracy)?.getChildAt(0) as? TextView

        // We'll update the text of the Virtual Docking Feed as an example of live changing data
        val virtualDockingSubtitle = findViewById<android.view.ViewGroup>(R.id.btn_virtual_docking_feed)?.let {
             val rightCol = it.getChildAt(1) as? android.view.ViewGroup
             rightCol?.getChildAt(1) as? TextView
        }

        lifecycleScope.launch {
            AiDataService.aiDiscoveriesCount.collectLatest { count ->
                aiDiscoveriesText?.text = "AI Discoveries\n(${count})"
            }
        }

        lifecycleScope.launch {
            AiDataService.modelAccuracy.collectLatest { accuracy ->
                modelAccuracyText?.text = "Model Accuracy\n(${accuracy}%)"
            }
        }

        lifecycleScope.launch {
            AiDataService.virtualDockingScore.collectLatest { score ->
                virtualDockingSubtitle?.text = "Real-time Binding Score: $score"
            }
        }
    }

    private fun setupDashboardInteractions() {
        // Notifications
        findViewById<View>(R.id.btn_notifications)?.setOnClickListener {
             startActivity(Intent(this, NotificationsActivity::class.java))
        }

        // Stats Chips
        findViewById<View>(R.id.chip_ai_discoveries)?.setOnClickListener {
             startActivity(Intent(this, AiDiscoveriesActivity::class.java))
        }

        findViewById<View>(R.id.chip_active_trials)?.setOnClickListener {
             startActivity(Intent(this, ActiveTrialsActivity::class.java))
        }

        findViewById<View>(R.id.chip_model_accuracy)?.setOnClickListener {
             startActivity(Intent(this, ModelAccuracyActivity::class.java))
        }

        // Hero Card - Molecular Docking
        findViewById<View>(R.id.card_molecular_docking_hero)?.setOnClickListener {
             startActivity(Intent(this, MolecularDockingActivity::class.java))
        }

        // Virtual Docking Feed
        findViewById<View>(R.id.btn_virtual_docking_feed)?.setOnClickListener {
             startActivity(Intent(this, VirtualDockingActivity::class.java))
        }



        // Quick Launch Buttons
        findViewById<View>(R.id.btn_quick_upload)?.setOnClickListener {
             startActivity(Intent(this, UploadReportActivity::class.java))
        }

        findViewById<View>(R.id.btn_quick_screening)?.setOnClickListener {
             startActivity(Intent(this, ScreeningActivity::class.java))
        }

        findViewById<View>(R.id.btn_quick_target_map)?.setOnClickListener {
             startActivity(Intent(this, TargetMapActivity::class.java))
        }

    }

    private fun setupNavigation() {
        findViewById<View>(R.id.nav_home)?.setOnClickListener {
            // Already on Home
        }
        
        findViewById<View>(R.id.nav_screening)?.setOnClickListener {
            startActivity(Intent(this, ScreeningActivity::class.java))
        }
        
        findViewById<View>(R.id.nav_targets)?.setOnClickListener {
            startActivity(Intent(this, TargetDiscoveryActivity::class.java))
        }
        
        findViewById<View>(R.id.nav_insights)?.setOnClickListener {
            startActivity(Intent(this, AiInsightsActivity::class.java))
        }
        
        findViewById<View>(R.id.nav_profile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
