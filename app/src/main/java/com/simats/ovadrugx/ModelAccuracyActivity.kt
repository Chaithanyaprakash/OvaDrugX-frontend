package com.simats.ovadrugx

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent

class ModelAccuracyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_model_accuracy)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<android.view.View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
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
            startActivity(Intent(this, TargetDiscoveryActivity::class.java))
        }

        findViewById<android.view.View>(R.id.nav_insights).setOnClickListener {
            // Already on Model Accuracy (part of Insights flow)
        }

        findViewById<android.view.View>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
