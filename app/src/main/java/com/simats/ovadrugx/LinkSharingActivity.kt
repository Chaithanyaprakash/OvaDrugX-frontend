package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class LinkSharingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_sharing)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btn_done).setOnClickListener {
            finish()
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        findViewById<View>(R.id.nav_home)?.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        
        findViewById<View>(R.id.nav_screening)?.setOnClickListener {
            startActivity(Intent(this, ScreeningActivity::class.java))
            finish()
        }
        
        findViewById<View>(R.id.nav_targets)?.setOnClickListener {
            startActivity(Intent(this, TargetDiscoveryActivity::class.java))
            finish()
        }
        
        findViewById<View>(R.id.nav_insights)?.setOnClickListener {
            startActivity(Intent(this, AiInsightsActivity::class.java))
            finish()
        }
        
        findViewById<View>(R.id.nav_profile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
}
