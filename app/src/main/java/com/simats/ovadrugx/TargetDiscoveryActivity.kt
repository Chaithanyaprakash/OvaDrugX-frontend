package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.ovadrugx.api.RetrofitClient
import com.simats.ovadrugx.model.PredictTargetsRequest
import com.simats.ovadrugx.model.PredictTargetsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TargetDiscoveryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target_discovery)

        // Handle back button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupBottomNavigation()
        setupClickListeners()
        handleIncomingMlData()
    }

    override fun onResume() {
        super.onResume()
        populateHistoricalTargets()
    }

    private fun populateHistoricalTargets() {
        val container = findViewById<android.widget.LinearLayout>(R.id.container_historical_targets)
        container?.removeAllViews()

        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", android.content.Context.MODE_PRIVATE)
        val currentGenes = sharedPrefs.getString("HISTORY_GENES", "") ?: ""
        val currentScores = sharedPrefs.getString("HISTORY_SCORES", "") ?: ""

        val geneList = currentGenes.split(",").filter { it.isNotEmpty() }
        val scoreList = currentScores.split(",").filter { it.isNotEmpty() }

        val displayGenes = if (geneList.isEmpty()) listOf("TP53", "BRCA1", "PARP1") else geneList
        val displayScores = if (scoreList.isEmpty()) listOf("92.0", "88.0", "75.0") else scoreList

        for (i in displayGenes.indices) {
            val gene = displayGenes[i]
            val score = if (i < displayScores.size) displayScores[i].toFloatOrNull() ?: 0f else 0f
            
            val cardView = androidx.cardview.widget.CardView(this).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, (16 * resources.displayMetrics.density).toInt())
                }
                setCardBackgroundColor(android.graphics.Color.WHITE)
                radius = 8 * resources.displayMetrics.density
                cardElevation = 2 * resources.displayMetrics.density
                tag = gene.lowercase() // For filtering
            }

            val mainLayout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                setPadding(
                    (16 * resources.displayMetrics.density).toInt(),
                    (16 * resources.displayMetrics.density).toInt(),
                    (16 * resources.displayMetrics.density).toInt(),
                    (16 * resources.displayMetrics.density).toInt()
                )
            }

            val textLayout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvSubtitle = android.widget.TextView(this).apply {
                text = "Target Molecule"
                setTextColor(android.graphics.Color.parseColor("#1976D2")) // primary_blue
                textSize = 12f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val tvTitle = android.widget.TextView(this).apply {
                text = gene
                setTextColor(android.graphics.Color.BLACK)
                textSize = 18f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, (4 * resources.displayMetrics.density).toInt(), 0, 0)
            }

            val tvPriority = android.widget.TextView(this).apply {
                text = "Confidence: ${score.toInt()}%"
                setTextColor(android.graphics.Color.parseColor("#757575")) // text_secondary
                textSize = 12f
                setPadding(0, (4 * resources.displayMetrics.density).toInt(), 0, 0)
            }

            val btnDocking = android.widget.TextView(this).apply {
                text = "Analyze Docking"
                setTextColor(android.graphics.Color.BLACK)
                textSize = 12f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setBackgroundResource(R.drawable.bg_rounded_border)
                setPadding(
                    (12 * resources.displayMetrics.density).toInt(),
                    (6 * resources.displayMetrics.density).toInt(),
                    (12 * resources.displayMetrics.density).toInt(),
                    (6 * resources.displayMetrics.density).toInt()
                )
                val params = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, (12 * resources.displayMetrics.density).toInt(), 0, 0)
                }
                layoutParams = params
                setOnClickListener {
                    val intent = Intent(this@TargetDiscoveryActivity, VirtualDockingActivity::class.java)
                    intent.putExtra("TARGET_NAME", gene)
                    startActivity(intent)
                }
            }

            textLayout.addView(tvSubtitle)
            textLayout.addView(tvTitle)
            textLayout.addView(tvPriority)
            textLayout.addView(btnDocking)

            val tvStatus = android.widget.TextView(this).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    (90 * resources.displayMetrics.density).toInt(),
                    (90 * resources.displayMetrics.density).toInt()
                )
                gravity = android.view.Gravity.CENTER
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
                
                if (i == 0) {
                    text = "Active"
                    setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                    setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"))
                } else {
                    text = "Completed"
                    setTextColor(android.graphics.Color.parseColor("#9E9E9E"))
                    setBackgroundColor(android.graphics.Color.parseColor("#F5F5F5"))
                }
            }

            mainLayout.addView(textLayout)
            mainLayout.addView(tvStatus)
            cardView.addView(mainLayout)

            container?.addView(cardView)
        }
    }

    private fun handleIncomingMlData() {
        val inputGene = intent.getStringExtra("ML_INPUT_GENE")
        if (inputGene != null) {
            val matchedGene = intent.getStringExtra("ML_MATCHED_GENE") ?: ""
            val organism = intent.getStringExtra("ML_ORGANISM") ?: ""
            val confidenceScore = intent.getDoubleExtra("ML_CONFIDENCE_SCORE", 0.0)
            val targetsList = intent.getStringArrayListExtra("ML_TARGETS_LIST") ?: arrayListOf()

            displayDynamicAiCard(inputGene, matchedGene, organism, confidenceScore, targetsList)
        }
    }

    private fun displayDynamicAiCard(inputGene: String, matchedGene: String, organism: String, confidenceScore: Double, targetsList: List<String>) {
        // Map data to the new dynamic XML elements we created
        val dynamicCard = findViewById<View>(R.id.card_dynamic_target)
        val tvSubtitle = findViewById<TextView>(R.id.tv_dynamic_subtitle)
        val tvTitle = findViewById<TextView>(R.id.tv_dynamic_title)
        val tvPriority = findViewById<TextView>(R.id.tv_dynamic_priority)
        val tvTargets = findViewById<TextView>(R.id.tv_dynamic_targets)
        val btnDocking = findViewById<TextView>(R.id.btn_docking_dynamic)

        dynamicCard.visibility = View.VISIBLE
        
        tvSubtitle.text = organism
        tvTitle.text = matchedGene
        tvPriority.text = "Confidence: ${confidenceScore}%"
        
        if (targetsList.isNotEmpty()) {
            tvTargets.text = targetsList.joinToString("\n") { "• $it" }
        } else {
            tvTargets.text = "No high confidence biological targets."
        }

        // Set up a custom virtual docking intent for whatever the dynamic gene is
        btnDocking.setOnClickListener {
            val intent = Intent(this, VirtualDockingActivity::class.java)
            intent.putExtra("TARGET_NAME", inputGene)
            startActivity(intent)
        }
    }

    private fun setupClickListeners() {
        // Notifications
        findViewById<View>(R.id.btn_notifications)?.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        // Search Implementation
        val searchInput = findViewById<EditText>(R.id.et_search_targets)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterTargets(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Live ML AI Querying
        searchInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.action == android.view.KeyEvent.ACTION_DOWN && event.keyCode == android.view.KeyEvent.KEYCODE_ENTER)) {
                
                val query = v.text.toString().trim()
                if (query.isNotEmpty()) {
                    fetchLiveAiTargets(query)
                }
                true
            } else false
        }
    }

    private fun fetchLiveAiTargets(geneName: String) {
        val request = PredictTargetsRequest(geneName)
        RetrofitClient.instance.predictTargets(request).enqueue(object : Callback<PredictTargetsResponse> {
            override fun onResponse(call: Call<PredictTargetsResponse>, response: Response<PredictTargetsResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    if (body.status == "success" && body.data != null) {
                        displayDynamicAiCard(
                            body.data.inputGene ?: geneName,
                            body.data.matchedGene ?: "",
                            body.data.organism ?: "",
                            body.data.confidenceScore ?: 0.0,
                            body.data.highConfidenceTargets ?: emptyList()
                        )
                    } else {
                        Toast.makeText(this@TargetDiscoveryActivity, body.message ?: "Gene not found in ML Model", Toast.LENGTH_SHORT).show()
                        findViewById<View>(R.id.card_dynamic_target).visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this@TargetDiscoveryActivity, "Failed to analyze gene", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PredictTargetsResponse>, t: Throwable) {
                Toast.makeText(this@TargetDiscoveryActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterTargets(query: String) {
        val lowerQuery = query.lowercase()

        // Clean up dynamic card if query is empty
        if (lowerQuery.isEmpty()) {
            findViewById<View>(R.id.card_dynamic_target)?.visibility = View.GONE
        }

        val container = findViewById<android.widget.LinearLayout>(R.id.container_historical_targets)
        if (container != null) {
            for (i in 0 until container.childCount) {
                val child = container.getChildAt(i)
                val tag = child.tag as? String ?: ""
                if (lowerQuery.isEmpty() || tag.contains(lowerQuery)) {
                    child.visibility = View.VISIBLE
                } else {
                    child.visibility = View.GONE
                }
            }
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
             // Already on Targets
             // startActivity(Intent(this, TargetDiscoveryActivity::class.java))
        }
        findViewById<android.view.View>(R.id.nav_insights).setOnClickListener {
             startActivity(Intent(this, AiInsightsActivity::class.java))
        }
        findViewById<android.view.View>(R.id.nav_profile).setOnClickListener {
             startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
