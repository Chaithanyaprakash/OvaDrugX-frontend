package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class KnowledgePathwayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_knowledge_pathway)

        findViewById<View?>(R.id.btn_back)?.setOnClickListener {
            finish()
        }

        findViewById<View?>(R.id.btn_notifications)?.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        try {
            setupClickListeners()
            setupBottomNavigation()
        } catch (e: Exception) {
            Toast.makeText(this, "Error setting up Knowledge Pathway", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            populateDiscoveries()
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading discoveries", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun setupClickListeners() {
        // Link filter chips
        findViewById<View?>(R.id.chip_pathways)?.setOnClickListener {
            startActivity(Intent(this, PathwaysActivity::class.java))
        }
        findViewById<View?>(R.id.chip_proteins)?.setOnClickListener {
            startActivity(Intent(this, ProteinsActivity::class.java))
        }
        findViewById<View?>(R.id.chip_drugs)?.setOnClickListener {
            startActivity(Intent(this, DrugsActivity::class.java))
        }

        // Research Paper Redirection
        findViewById<View?>(R.id.row_research_tumour)?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pmc.ncbi.nlm.nih.gov/articles/PMC6801830/"))
            startActivity(intent)
        }

        // Novel Biomarkers Redirection
        findViewById<View?>(R.id.row_research_biomarkers)?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pmc.ncbi.nlm.nih.gov/articles/PMC12647161/"))
            startActivity(intent)
        }
    }

    fun goToPathways(view: View) {
        startActivity(Intent(this, PathwaysActivity::class.java))
    }

    private fun populateDiscoveries() {
        val containerDiscoveries = findViewById<android.widget.LinearLayout>(R.id.container_latest_discoveries) ?: return
        containerDiscoveries.removeAllViews()

        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", android.content.Context.MODE_PRIVATE)
        val genes = sharedPrefs.getString("HISTORY_GENES", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
        val drugs = sharedPrefs.getString("HISTORY_DRUGS", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()

        if (genes.isEmpty()) {
            return
        }

        for (i in genes.indices) {
            val geneName = genes[i]
            val drugName = drugs.getOrNull(i) ?: "Unknown Compound"

            val rowLayout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, (14 * resources.displayMetrics.density).toInt(), 0, (14 * resources.displayMetrics.density).toInt())
                isClickable = true
                isFocusable = true
                setBackgroundResource(android.R.attr.selectableItemBackground)
                setOnClickListener {
                    startActivity(Intent(this@KnowledgePathwayActivity, DrugsActivity::class.java))
                }
            }

            val tvDrugName = android.widget.TextView(this).apply {
                text = drugName
                setTextColor(android.graphics.Color.parseColor("#263238"))
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvGeneName = android.widget.TextView(this).apply {
                text = geneName
                setTextColor(android.graphics.Color.parseColor("#546E7A"))
                textSize = 14f
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            rowLayout.addView(tvDrugName)
            rowLayout.addView(tvGeneName)
            containerDiscoveries.addView(rowLayout)

            // Add separator if not the last item
            if (i < genes.size - 1) {
                val separator = View(this).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        (1 * resources.displayMetrics.density).toInt()
                    )
                    setBackgroundColor(android.graphics.Color.parseColor("#ECEFF1"))
                }
                containerDiscoveries.addView(separator)
            }
        }
    }

    private fun setupBottomNavigation() {
        findViewById<View?>(R.id.nav_home)?.setOnClickListener {
             startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<View?>(R.id.nav_screening)?.setOnClickListener {
             startActivity(Intent(this, ScreeningActivity::class.java))
        }
        findViewById<View?>(R.id.nav_targets)?.setOnClickListener {
             startActivity(Intent(this, TargetDiscoveryActivity::class.java))
        }
        findViewById<View?>(R.id.nav_insights)?.setOnClickListener {
             startActivity(Intent(this, AiInsightsActivity::class.java))
        }
        findViewById<View?>(R.id.nav_profile)?.setOnClickListener {
             startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
