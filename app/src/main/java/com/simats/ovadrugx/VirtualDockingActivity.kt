package com.simats.ovadrugx

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class VirtualDockingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_docking)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<android.view.View>(R.id.btn_notifications).setOnClickListener {
            startActivity(android.content.Intent(this, NotificationsActivity::class.java))
        }

        // Retrieve Target Name from Intent if provided
        val targetName = intent.getStringExtra("TARGET_NAME")
        
        val etSearchProtein = findViewById<android.widget.EditText>(R.id.et_search_protein)
        val llSelectedProteinChip = findViewById<android.view.View>(R.id.ll_selected_protein_chip)
        
        if (targetName != null) {
            etSearchProtein?.setText(targetName)
            llSelectedProteinChip?.visibility = android.view.View.VISIBLE
            
            val tvSelectedProtein = findViewById<android.widget.TextView>(R.id.tv_selected_protein)
            tvSelectedProtein?.text = "$targetName (Simulated) - Human"
        } else {
            llSelectedProteinChip?.visibility = android.view.View.GONE
        }

        // Automatically route the Target Name over to the Screening Engine to scan the DB
        findViewById<android.view.View>(R.id.btnRunDocking).setOnClickListener {
            val intent = android.content.Intent(this, ScreeningActivity::class.java)
            if (targetName != null) {
                intent.putExtra("AUTO_SCAN_GENE", targetName)
            }
            startActivity(intent)
            finish()
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        findViewById<android.view.View>(R.id.nav_home)?.setOnClickListener {
            startActivity(android.content.Intent(this, HomeActivity::class.java))
            finish()
        }
        
        findViewById<android.view.View>(R.id.nav_screening)?.setOnClickListener {
            startActivity(android.content.Intent(this, ScreeningActivity::class.java))
            finish()
        }
        
        findViewById<android.view.View>(R.id.nav_targets)?.setOnClickListener {
            startActivity(android.content.Intent(this, TargetDiscoveryActivity::class.java))
            finish()
        }
        
        findViewById<android.view.View>(R.id.nav_insights)?.setOnClickListener {
            startActivity(android.content.Intent(this, AiInsightsActivity::class.java))
            finish()
        }
        
        findViewById<android.view.View>(R.id.nav_profile)?.setOnClickListener {
            startActivity(android.content.Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
}
