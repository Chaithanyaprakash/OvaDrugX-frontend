package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.simats.ovadrugx.api.RetrofitClient
import com.simats.ovadrugx.model.LoginRequest
import com.simats.ovadrugx.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        // Explicitly allow screenshots
        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)

        val sharedPrefs = getSharedPreferences("OvaDrugXPrefs", android.content.Context.MODE_PRIVATE)
        val isBiometricEnabled = sharedPrefs.getBoolean("biometric_enabled", false)
        if (isBiometricEnabled) {
            val executor = androidx.core.content.ContextCompat.getMainExecutor(this)
            val biometricPrompt = androidx.biometric.BiometricPrompt(this, executor,
                object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                        
                        // Proceed to Home
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                })

            val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for OvaDrugX")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build()

            biometricPrompt.authenticate(promptInfo)
        }

        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)

        findViewById<AppCompatButton>(R.id.btn_sign_in).setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            android.util.Log.d("LoginActivity", "Attempting login for: $email")
            RetrofitClient.instance.login(LoginRequest(email = email, password = password))
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        android.util.Log.d("LoginActivity", "Response Code: ${response.code()}")
                        if (response.isSuccessful && response.body()?.status == "success") {
                            android.util.Log.d("LoginActivity", "Login Success")
                            
                            // Save user data
                            val sharedPreferences = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            response.body()?.data?.let { user ->
                                editor.putString("full_name", user.fullName)
                                editor.putString("email", user.email)
                                editor.putString("mobile", user.mobile)
                                editor.apply()
                            }
                            
                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            finish()
                        } else {
                            val msg = response.body()?.message ?: "Login Failed"
                            android.util.Log.e("LoginActivity", "Login Failed: $msg")
                            Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        android.util.Log.e("LoginActivity", "Network Error: ${t.message}", t)
                        Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        findViewById<android.view.View>(R.id.ll_create_account)?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        findViewById<TextView>(R.id.tv_forgot_password).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}
