package com.simats.ovadrugx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        
        
        // Handle back button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val notificationsContainer = findViewById<android.view.View>(R.id.notifications_container)
        val emptyStateObj = findViewById<android.view.View>(R.id.tv_empty_state)
        val markAsReadBtn = findViewById<android.view.View>(R.id.btn_mark_as_read)

        markAsReadBtn.setOnClickListener {
            notificationsContainer.visibility = android.view.View.GONE
            emptyStateObj.visibility = android.view.View.VISIBLE
            // Optionally, hide the "Mark as read" button too since there's nothing to read
            markAsReadBtn.visibility = android.view.View.GONE
        }
    }
}
