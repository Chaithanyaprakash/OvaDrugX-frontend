package com.simats.ovadrugx

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ModelSelectionActivity : AppCompatActivity() {

    private lateinit var btnSelectModel1: Button
    private lateinit var btnSelectModel2: Button
    private lateinit var btnSelectModel3: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_selection)

        // Initialize Views
        btnSelectModel1 = findViewById(R.id.btnSelectModel1)
        btnSelectModel2 = findViewById(R.id.btnSelectModel2)
        btnSelectModel3 = findViewById(R.id.btnSelectModel3)

        // Handle Back Button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<android.view.View>(R.id.btn_notifications).setOnClickListener {
            startActivity(android.content.Intent(this, NotificationsActivity::class.java))
        }

        // Load saved selection
        loadSelection()

        // Set Click Listeners
        btnSelectModel1.setOnClickListener {
            selectModel(1)
        }
        btnSelectModel2.setOnClickListener {
            selectModel(2)
        }
        btnSelectModel3.setOnClickListener {
            selectModel(3)
        }
    }

    private fun loadSelection() {
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val selectedModel = sharedPref.getInt("selected_model", 1) // Default to 1
        selectModel(selectedModel)
    }

    private fun selectModel(modelId: Int) {
        // Reset all buttons
        resetButton(btnSelectModel1)
        resetButton(btnSelectModel2)
        resetButton(btnSelectModel3)

        // Highlight selected button
        when (modelId) {
            1 -> highlightButton(btnSelectModel1)
            2 -> highlightButton(btnSelectModel2)
            3 -> highlightButton(btnSelectModel3)
        }

        // Save selection
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("selected_model", modelId)
            apply()
        }
    }

    private fun resetButton(button: Button) {
        button.text = "Select"
        button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.stats_bg) // #EEEEEE
        button.setTextColor(ContextCompat.getColor(this, R.color.black))
    }

    private fun highlightButton(button: Button) {
        button.text = "Selected"
        button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary_blue) // #1976D2
        button.setTextColor(ContextCompat.getColor(this, R.color.white))
    }
}
