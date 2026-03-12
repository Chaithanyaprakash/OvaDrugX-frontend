package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ReportSignedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_signed)
        setupNavigation()

        findViewById<ImageView>(R.id.btn_close).setOnClickListener {
            navigateToHome()
        }

        // Automatically navigate to Home after 3 seconds
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (!isDestroyed && !isFinishing) {
                navigateToHome()
            }
        }, 3000)

        // Placeholder listeners for other buttons
        findViewById<android.view.View>(R.id.btn_view_report).setOnClickListener {
            // Logic to view report
        }

         findViewById<android.view.View>(R.id.btn_download_pdf).setOnClickListener {
            // Logic to download PDF
        }

         findViewById<android.view.View>(R.id.btn_share_report).setOnClickListener {
            // Logic to share report
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun setupNavigation() {
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
