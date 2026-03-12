package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.simats.ovadrugx.api.RetrofitClient
import com.simats.ovadrugx.model.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val etEmail = findViewById<android.widget.EditText>(R.id.et_fp_email)
        val btnGetOtp = findViewById<AppCompatButton>(R.id.btn_get_otp)

        btnGetOtp.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call API
            RetrofitClient.instance.forgotPassword(com.simats.ovadrugx.model.ForgotPasswordRequest(email))
                .enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null && body.error != "true") {
                                val msg = body.message ?: "OTP Sent"
                                Toast.makeText(this@ForgotPasswordActivity, msg, Toast.LENGTH_LONG).show()

                                val intent = Intent(this@ForgotPasswordActivity, ForgotPasswordOtpActivity::class.java)
                                intent.putExtra("email", email)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@ForgotPasswordActivity, body?.message ?: "Failed", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                             Toast.makeText(this@ForgotPasswordActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                         Toast.makeText(this@ForgotPasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        findViewById<android.view.View>(R.id.ll_create_account)?.setOnClickListener {
            finish()
        }
    }
}
