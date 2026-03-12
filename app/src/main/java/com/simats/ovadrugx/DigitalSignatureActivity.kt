package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class DigitalSignatureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_digital_signature)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        val drawingView = findViewById<DrawingView>(R.id.drawing_view)
        val tvSignPrompt = findViewById<android.widget.TextView>(R.id.tv_sign_prompt)

        // Hide "Sign Here" text when user touches the canvas
        drawingView.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                tvSignPrompt.visibility = View.GONE
            }
            // Need to return false so DrawingView still handles the touch
            false
        }

        findViewById<View>(R.id.btn_clear).setOnClickListener {
            drawingView.clear()
            tvSignPrompt.visibility = View.VISIBLE
        }

        findViewById<View>(R.id.btn_sign_finalize).setOnClickListener {
            try {
                val bitmap = drawingView.getBitmap()
                val file = java.io.File(cacheDir, "signature.png")
                val fos = java.io.FileOutputStream(file)
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
                android.widget.Toast.makeText(this, "Signature Saved", android.widget.Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finish()
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        findViewById<View>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        findViewById<View>(R.id.nav_screening).setOnClickListener {
            startActivity(Intent(this, ScreeningActivity::class.java))
        }

        findViewById<View>(R.id.nav_targets).setOnClickListener {
            startActivity(Intent(this, TargetDiscoveryActivity::class.java))
        }

        findViewById<View>(R.id.nav_insights).setOnClickListener {
            startActivity(Intent(this, AiInsightsActivity::class.java))
        }

        findViewById<View>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
