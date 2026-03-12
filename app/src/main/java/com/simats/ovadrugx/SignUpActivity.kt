package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import android.util.Log
import com.simats.ovadrugx.api.RetrofitClient
import com.simats.ovadrugx.model.RegisterRequest
import com.simats.ovadrugx.model.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        
        // Explicitly allow screenshots
        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)

        val etFullName = findViewById<EditText>(R.id.et_fullname)
        val etEmail = findViewById<EditText>(R.id.et_signup_email)
        val etMobile = findViewById<EditText>(R.id.et_mobile)
        val etPassword = findViewById<EditText>(R.id.et_signup_password)
        val etConfirmPassword = findViewById<EditText>(R.id.et_signup_confirm_password)
        val btnSignUp = findViewById<AppCompatButton>(R.id.btn_sign_up)
        val tvLoginLink = findViewById<android.view.View>(R.id.tv_login_link)

        btnSignUp.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create Request Object
            val request = RegisterRequest(fullName, email, mobile, password, confirmPassword)

            // API Call
            Log.d("SignUpActivity", "Attempting to register: $email")
            RetrofitClient.instance.register(request).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    Log.d("SignUpActivity", "Response Code: ${response.code()}")
                    if (response.isSuccessful) {
                        val body = response.body()
                        android.util.Log.d("SignUpActivity", "Response Body: $body")
                        
                        // Navigate if response is successful (HTTP 200) and error is not "true"
                        // We check for "true" string explicitly. If null, we assume success or check message.
                        if (body != null && body.error != "true") {
                            val msg = body.message ?: "Registered!"
                            android.widget.Toast.makeText(this@SignUpActivity, msg, android.widget.Toast.LENGTH_LONG).show()
                            
                            // Navigate to OTP Verification
                            val intent = Intent(this@SignUpActivity, OtpVerificationActivity::class.java)
                            intent.putExtra("email", email) 
                            intent.putExtra("is_forgot_password", false)
                            startActivity(intent)
                            finish()
                        } else {
                            val msg = body?.message ?: "Registration failed"
                            android.util.Log.e("SignUpActivity", "Server reported error: $msg")
                            android.widget.Toast.makeText(this@SignUpActivity, msg, android.widget.Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        android.util.Log.e("SignUpActivity", "Registration failed: ${response.code()} - $errorBody")
                        android.widget.Toast.makeText(this@SignUpActivity, "Server Error: ${response.code()}", android.widget.Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.e("SignUpActivity", "Network Error: ${t.message}", t)
                    Toast.makeText(this@SignUpActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        tvLoginLink.setOnClickListener {
            finish() // Go back to login
        }
    }
}
