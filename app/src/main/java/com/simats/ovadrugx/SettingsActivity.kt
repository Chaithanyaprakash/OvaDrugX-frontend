package com.simats.ovadrugx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.widget.SeekBar
import com.simats.ovadrugx.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back Button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Model Selection
        binding.btnModelSelection.setOnClickListener {
            startActivity(Intent(this, ModelSelectionActivity::class.java))
        }

        // Accuracy vs Speed Slider
        binding.seekBarAccuracy.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvAccuracyValue.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Bottom Navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        binding.navScreening.setOnClickListener {
            startActivity(Intent(this, ScreeningActivity::class.java))
        }
        binding.navTargets.setOnClickListener {
            startActivity(Intent(this, TargetDiscoveryActivity::class.java))
        }
        binding.navInsights.setOnClickListener {
            startActivity(Intent(this, AiInsightsActivity::class.java))
        }
        binding.navProfile.setOnClickListener {
             // Return to main Profile page
             finish() 
        }
    }
}
