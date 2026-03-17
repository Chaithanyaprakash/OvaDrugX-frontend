package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        setupBottomNavigation()
        setupMenuActions()

        findViewById<View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val sharedPreferences = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "") ?: ""

        if (email.isEmpty()) return

        com.simats.ovadrugx.api.RetrofitClient.instance
            .getAccount(com.simats.ovadrugx.model.GetAccountRequest(email))
            .enqueue(object : retrofit2.Callback<com.simats.ovadrugx.model.GetAccountResponse> {
                override fun onResponse(
                    call: retrofit2.Call<com.simats.ovadrugx.model.GetAccountResponse>,
                    response: retrofit2.Response<com.simats.ovadrugx.model.GetAccountResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val data = response.body()?.data
                        if (data != null) {
                            val fullName = data.full_name
                            val department = data.department

                            findViewById<android.widget.TextView>(R.id.tv_name).text = fullName
                            findViewById<android.widget.TextView>(R.id.tv_department).text = department

                            val imgProfile = findViewById<android.widget.ImageView>(R.id.img_profile)
                            if (isFemaleName(fullName)) {
                                imgProfile.setImageResource(R.drawable.ic_profile_female)
                            } else {
                                imgProfile.setImageResource(R.drawable.ic_profile_male)
                            }

                            // Sync to SharedPreferences for other screens
                            sharedPreferences.edit().apply {
                                putString("full_name", fullName)
                                putString("department", department)
                                putString("gender", data.gender)
                                apply()
                            }
                        }
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<com.simats.ovadrugx.model.GetAccountResponse>,
                    t: Throwable
                ) {
                    // Fallback to cached data if network fails
                    val fullName = sharedPreferences.getString("full_name", "User")
                    val department = sharedPreferences.getString("department", "Oncologist & Lead Researcher")
                    findViewById<android.widget.TextView>(R.id.tv_name).text = fullName
                    findViewById<android.widget.TextView>(R.id.tv_department).text = department
                }
            })
    }

    private fun isFemaleName(fullName: String): Boolean {
        val name = fullName.lowercase()
        if (name.contains("mrs.") || name.contains("ms.") || name.contains("miss")) return true
        if (name.contains("mr.")) return false

        val words = name.replace("dr.", "").trim().split(" ")
        val firstName = words.firstOrNull() ?: return false
        
        val femaleEndings = listOf("a", "i", "ee", "ia", "ya", "na", "ta")
        val maleExceptions = listOf("aditya", "krishna", "shiva", "datta", "chandra", "surya", "chaitanya")

        if (maleExceptions.contains(firstName)) return false
        return femaleEndings.any { firstName.endsWith(it) }
    }

    private fun setupMenuActions() {
        // Account Information
        findViewById<View>(R.id.btn_account_info).setOnClickListener {
            startActivity(Intent(this, AccountInformationActivity::class.java))
        }

        // Security & Biometrics
        findViewById<View>(R.id.btn_security).setOnClickListener {
            startActivity(Intent(this, SecurityActivity::class.java))
        }

        // Privacy Policy
        findViewById<View>(R.id.btn_privacy).setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }

        // Help & Support
        findViewById<View>(R.id.btn_help).setOnClickListener {
            startActivity(Intent(this, HelpSupportActivity::class.java))
        }

        // Log Out
        findViewById<View>(R.id.btn_logout).setOnClickListener {
             val intent = Intent(this, LoginActivity::class.java)
             intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
             startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
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
             // Already on Profile
        }
    }
}
