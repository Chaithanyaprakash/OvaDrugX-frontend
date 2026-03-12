package com.simats.ovadrugx

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent

class TargetMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_target_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }
        
        setupNavigation()
    }

    private fun setupNavigation() {
        findViewById<android.view.View>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        findViewById<android.view.View>(R.id.nav_screening).setOnClickListener {
            startActivity(Intent(this, ScreeningActivity::class.java))
        }

        findViewById<android.view.View>(R.id.nav_targets).setOnClickListener {
            // Already on Targets page
        }

        findViewById<android.view.View>(R.id.nav_insights).setOnClickListener {
            startActivity(Intent(this, AiInsightsActivity::class.java))
        }

        findViewById<android.view.View>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
