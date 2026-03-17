package com.simats.ovadrugx

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.ovadrugx.databinding.ActivityUpdateAccountBinding

class UpdateAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate binding and content view first; this is very unlikely to fail.
        binding = ActivityUpdateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        // Lock email field as requested
        binding.etEmail.isEnabled = false
        binding.etEmail.isFocusable = false

        // Provide visual indicator
        binding.etEmail.alpha = 0.6f

        try {
            val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val email = sharedPreferences.getString("email", "") ?: ""

            if (email.isEmpty()) {
                Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show()
                return
            }

            binding.etEmail.setText(email)
            setupGenderDropdown()
            loadCurrentData(email)

            binding.btnSaveChanges.setOnClickListener {
                saveChanges(email)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading account data", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun setupGenderDropdown() {
        val genders = arrayOf("Male", "Female", "Others")
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_list_item_1, genders)
        binding.etGender.setAdapter(adapter)
        
        // Show dropdown when clicked or focused
        binding.etGender.setOnClickListener { binding.etGender.showDropDown() }
        binding.etGender.setOnFocusChangeListener { _, hasFocus -> 
            if (hasFocus) binding.etGender.showDropDown() 
        }
    }

    private fun loadCurrentData(email: String) {
        com.simats.ovadrugx.api.RetrofitClient.instance.getAccount(com.simats.ovadrugx.model.GetAccountRequest(email))
            .enqueue(object : retrofit2.Callback<com.simats.ovadrugx.model.GetAccountResponse> {
                override fun onResponse(
                    call: retrofit2.Call<com.simats.ovadrugx.model.GetAccountResponse>,
                    response: retrofit2.Response<com.simats.ovadrugx.model.GetAccountResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val data = response.body()?.data
                        if (data != null) {
                            binding.etFullName.setText(data.full_name)
                            binding.etGender.setText(data.gender, false)
                            binding.etDepartment.setText(data.department)
                            binding.etLicenseNumber.setText(data.license_number)
                            binding.etMobile.setText(data.mobile ?: "")
                        }
                    } else {
                        Toast.makeText(this@UpdateAccountActivity, "Failed to load current data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<com.simats.ovadrugx.model.GetAccountResponse>, t: Throwable) {
                    Toast.makeText(this@UpdateAccountActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun saveChanges(email: String) {
        val fullName = binding.etFullName.text.toString().trim()
        val mobile = binding.etMobile.text.toString().trim()
        val gender = binding.etGender.text.toString().trim()
        val department = binding.etDepartment.text.toString().trim()
        val licenseNumber = binding.etLicenseNumber.text.toString().trim()

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Full Name is required", Toast.LENGTH_SHORT).show()
            return
        }

        val request = com.simats.ovadrugx.model.UpdateAccountRequest(
            email = email,
            full_name = fullName,
            gender = gender,
            department = department,
            license_number = licenseNumber,
            mobile = mobile
        )

        binding.btnSaveChanges.isEnabled = false
        binding.btnSaveChanges.text = "Saving..."
        Toast.makeText(this, "Updating account...", Toast.LENGTH_SHORT).show()

        com.simats.ovadrugx.api.RetrofitClient.instance.updateAccount(request)
            .enqueue(object : retrofit2.Callback<com.simats.ovadrugx.model.GenericResponse> {
                override fun onResponse(
                    call: retrofit2.Call<com.simats.ovadrugx.model.GenericResponse>,
                    response: retrofit2.Response<com.simats.ovadrugx.model.GenericResponse>
                ) {
                    binding.btnSaveChanges.isEnabled = true
                    binding.btnSaveChanges.text = "Save Changes"

                    if (response.isSuccessful && response.body()?.status == "success") {
                        
                        // Sync all updated fields to local session cache for immediate UI updates
                        val sharedSharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                        sharedSharedPreferences.edit().apply {
                            putString("full_name", fullName)
                            putString("department", department)
                            putString("gender", gender)
                            putString("license_number", licenseNumber)
                            putString("mobile", mobile)
                            apply()
                        }

                        Toast.makeText(this@UpdateAccountActivity, "Account updated successfully!", Toast.LENGTH_SHORT).show()
                        
                        // Redirect to Account Information
                        val intent = android.content.Intent(this@UpdateAccountActivity, AccountInformationActivity::class.java)
                        intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@UpdateAccountActivity, response.body()?.message ?: "Update failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<com.simats.ovadrugx.model.GenericResponse>, t: Throwable) {
                    binding.btnSaveChanges.isEnabled = true
                    binding.btnSaveChanges.text = "Save Changes"
                    Toast.makeText(this@UpdateAccountActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
