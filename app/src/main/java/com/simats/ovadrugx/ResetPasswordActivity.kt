package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val etNewPassword = findViewById<android.widget.EditText>(R.id.et_new_password)
        val etConfirmPassword = findViewById<android.widget.EditText>(R.id.et_confirm_new_password)
        val btnContinue = findViewById<AppCompatButton>(R.id.btn_continue_reset)
        
        val email = intent.getStringExtra("email") ?: ""

        btnContinue.setOnClickListener {
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                android.widget.Toast.makeText(this, "Please enter new password", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (newPassword != confirmPassword) {
                android.widget.Toast.makeText(this, "Passwords do not match", android.widget.Toast.LENGTH_SHORT).show()
                 return@setOnClickListener
            }
            
            if (newPassword.length < 6) {
                android.widget.Toast.makeText(this, "Password too short", android.widget.Toast.LENGTH_SHORT).show()
                 return@setOnClickListener
            }

            // Call API
            val request = com.simats.ovadrugx.model.ResetPasswordRequest(email = email, newPassword = newPassword)
            com.simats.ovadrugx.api.RetrofitClient.instance.resetPassword(request)
                .enqueue(object : retrofit2.Callback<com.simats.ovadrugx.model.GenericResponse> {
                    override fun onResponse(call: retrofit2.Call<com.simats.ovadrugx.model.GenericResponse>, response: retrofit2.Response<com.simats.ovadrugx.model.GenericResponse>) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            android.widget.Toast.makeText(this@ResetPasswordActivity, "Password reset successfully", android.widget.Toast.LENGTH_LONG).show()
                            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            android.widget.Toast.makeText(this@ResetPasswordActivity, response.body()?.message ?: "Reset Failed", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<com.simats.ovadrugx.model.GenericResponse>, t: Throwable) {
                         android.widget.Toast.makeText(this@ResetPasswordActivity, "Error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
