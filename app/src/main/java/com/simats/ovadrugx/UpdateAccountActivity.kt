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
                            binding.etGender.setText(data.gender)
                            binding.etDepartment.setText(data.department)
                            binding.etLicenseNumber.setText(data.license_number)
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
            license_number = licenseNumber
        )

        binding.btnSaveChanges.isEnabled = false
        binding.btnSaveChanges.text = "Saving..."

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
                        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                        sharedPreferences.edit().apply {
                            putString("full_name", fullName)
                            putString("gender", gender)
                            putString("department", department)
                            putString("license_number", licenseNumber)
                            apply()
                        }

                        Toast.makeText(this@UpdateAccountActivity, "Account updated in database successfully!", Toast.LENGTH_SHORT).show()
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
