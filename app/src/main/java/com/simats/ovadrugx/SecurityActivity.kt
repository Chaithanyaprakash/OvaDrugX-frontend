package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class SecurityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)

        // Handle Back Button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Change Password Button
        findViewById<AppCompatButton>(R.id.btnChangePassword).setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        // Biometrics Switch
        val switchBiometrics = findViewById<android.widget.Switch>(R.id.switchBiometrics)
        val sharedPrefs = getSharedPreferences("OvaDrugXPrefs", android.content.Context.MODE_PRIVATE)
        
        switchBiometrics.isChecked = sharedPrefs.getBoolean("biometric_enabled", false)

        switchBiometrics.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Check if device supports biometrics before enabling
                val biometricManager = androidx.biometric.BiometricManager.from(this)
                when (biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                    androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> {
                        sharedPrefs.edit().putBoolean("biometric_enabled", true).apply()
                        android.widget.Toast.makeText(this, "Biometric login enabled", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        switchBiometrics.isChecked = false
                        android.widget.Toast.makeText(this, "Biometrics not set up or available on this device", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                sharedPrefs.edit().putBoolean("biometric_enabled", false).apply()
                android.widget.Toast.makeText(this, "Biometric login disabled", android.widget.Toast.LENGTH_SHORT).show()
            }
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
            // Return to main Profile page or just finish since we are in a sub-page of profile context
            finish() 
        }
    }
}
