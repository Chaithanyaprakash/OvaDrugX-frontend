package com.simats.ovadrugx

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            // Navigate to Report Signed Success Page
            startActivity(android.content.Intent(this, ReportSignedActivity::class.java))
            finish()
        }
    }
}
