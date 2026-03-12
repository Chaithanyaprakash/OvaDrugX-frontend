package com.simats.ovadrugx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import com.simats.ovadrugx.databinding.ActivityAccountInformationBinding

class AccountInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back Button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Initialize fields as read-only
        setFieldsEnabled(false)

        // Edit Button redirects to UpdateAccountActivity
        binding.btnEdit.setOnClickListener {
            startActivity(Intent(this, UpdateAccountActivity::class.java))
        }

        // Bottom Navigation Logic
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        // Read the basic email from the user session
        val sharedPreferences = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "") ?: ""

        if (email.isEmpty()) {
            android.widget.Toast.makeText(this, "Session expired. Not logged in.", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        binding.etEmail.setText(email)
        binding.tvProfileName.text = "Loading..."
        binding.etFullName.setText("...")
        binding.etGender.setText("...")
        binding.etDepartment.setText("...")
        binding.etLicenseNumber.setText("...")

        try {
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
                                binding.tvProfileName.text = data.full_name
                                binding.etFullName.setText(data.full_name)
                                binding.etGender.setText(data.gender)
                                binding.etDepartment.setText(data.department)
                                binding.etLicenseNumber.setText(data.license_number)

                                if (isFemaleName(data.full_name)) {
                                    binding.ivProfileImage.setImageResource(R.drawable.ic_profile_female)
                                } else {
                                    binding.ivProfileImage.setImageResource(R.drawable.ic_profile_male)
                                }
                            } else {
                                android.widget.Toast.makeText(
                                    this@AccountInformationActivity,
                                    "Failed to load profile",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            android.widget.Toast.makeText(
                                this@AccountInformationActivity,
                                "Failed to load profile",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: retrofit2.Call<com.simats.ovadrugx.model.GetAccountResponse>,
                        t: Throwable
                    ) {
                        android.widget.Toast.makeText(
                            this@AccountInformationActivity,
                            "Network Error: ${t.message}",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                this@AccountInformationActivity,
                "Error loading profile: ${e.message}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setFieldsEnabled(enabled: Boolean) {
        binding.etFullName.isEnabled = enabled
        binding.etEmail.isEnabled = enabled
        binding.etGender.isEnabled = enabled
        binding.etDepartment.isEnabled = enabled
        binding.etLicenseNumber.isEnabled = enabled
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
            // Already in profile section (albeit sub-page), maybe do nothing or go back to main profile
             finish() // Going back to ProfileActivity seems appropriate since this is a sub-activity
        }
    }
}
