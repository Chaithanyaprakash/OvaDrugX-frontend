package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class HelpSupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)

        // Contact Support Button
        findViewById<android.widget.Button>(R.id.btnContactSupport).setOnClickListener {
            startChat("")
        }

        // Quick Queries
        findViewById<android.view.View>(R.id.btnQueryTech).setOnClickListener {
            startChat("Technical Issue")
        }
        findViewById<android.view.View>(R.id.btnQueryAi).setOnClickListener {
            startChat("AI Model Help")
        }
        findViewById<android.view.View>(R.id.btnQueryAccount).setOnClickListener {
            startChat("Account Access")
        }
        findViewById<android.view.View>(R.id.btnQueryBug).setOnClickListener {
            startChat("Bug Report")
        }

        // FAQs
        findViewById<android.view.View>(R.id.btnFaq1).setOnClickListener {
            startChat("How to use the drug screening tool?")
        }
        findViewById<android.view.View>(R.id.btnFaq2).setOnClickListener {
            startChat("What are the target discovery methods?")
        }
        findViewById<android.view.View>(R.id.btnFaq3).setOnClickListener {
            startChat("How to interpret the results?")
        }
        findViewById<android.view.View>(R.id.btnFaq4).setOnClickListener {
            startChat("Troubleshooting common issues")
        }

        // Bottom Navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navScreening).setOnClickListener {
            startActivity(Intent(this, ScreeningActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navTargets).setOnClickListener {
            startActivity(Intent(this, TargetDiscoveryActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navInsights).setOnClickListener {
            startActivity(Intent(this, AiInsightsActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener {
            // Return to main Profile page or just finish
            finish() 
        }
    }

    private fun startChat(initialMessage: String) {
        val intent = Intent(this, SupportChatActivity::class.java)
        intent.putExtra("EXT_CHAT_MESSAGE", initialMessage)
        startActivity(intent)
    }
}
