package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class ForgotPasswordOtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_otp)

        findViewById<ImageView>(R.id.btn_back_otp).setOnClickListener {
            finish()
        }

        val otp1 = findViewById<android.widget.EditText>(R.id.otp_1)
        val otp2 = findViewById<android.widget.EditText>(R.id.otp_2)
        val otp3 = findViewById<android.widget.EditText>(R.id.otp_3)
        val otp4 = findViewById<android.widget.EditText>(R.id.otp_4)
        val otp5 = findViewById<android.widget.EditText>(R.id.otp_5)
        val otp6 = findViewById<android.widget.EditText>(R.id.otp_6)
        val tvResendCode = findViewById<TextView>(R.id.tv_resend_code)
        val btnVerify = findViewById<AppCompatButton>(R.id.btn_verify_otp)

        setupOtpInputs(otp1, otp2, otp3, otp4, otp5, otp6)
        startResendTimer(tvResendCode)

        btnVerify.setOnClickListener {
            val code = "${otp1.text}${otp2.text}${otp3.text}${otp4.text}${otp5.text}${otp6.text}"
            if (code.length < 6) {
                android.widget.Toast.makeText(this, "Please enter full 6-digit code", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = intent.getStringExtra("email") ?: ""

             // Verify Reset OTP
             val request = com.simats.ovadrugx.model.VerifyResetOtpRequest(email = email, otp = code)
             com.simats.ovadrugx.api.RetrofitClient.instance.verifyResetOtp(request)
                 .enqueue(object : retrofit2.Callback<com.simats.ovadrugx.model.GenericResponse> {
                     override fun onResponse(call: retrofit2.Call<com.simats.ovadrugx.model.GenericResponse>, response: retrofit2.Response<com.simats.ovadrugx.model.GenericResponse>) {
                         if (response.isSuccessful && response.body()?.status == "success") {
                            // Navigate to Reset Password
                            val intent = Intent(this@ForgotPasswordOtpActivity, ResetPasswordActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                         } else {
                             android.widget.Toast.makeText(this@ForgotPasswordOtpActivity, response.body()?.message ?: "Invalid OTP", android.widget.Toast.LENGTH_SHORT).show()
                         }
                     }

                     override fun onFailure(call: retrofit2.Call<com.simats.ovadrugx.model.GenericResponse>, t: Throwable) {
                          android.widget.Toast.makeText(this@ForgotPasswordOtpActivity, "Error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                     }
                 })
        }
    }

    private fun setupOtpInputs(vararg editTexts: android.widget.EditText) {
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    } else if (s?.length == 0 && i > 0) {
                        editTexts[i - 1].requestFocus()
                    }
                }
                override fun afterTextChanged(s: android.text.Editable?) {}
            })
            
            // Handle backspace key to move focus back even if empty
            editTexts[i].setOnKeyListener { v, keyCode, event ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && event.action == android.view.KeyEvent.ACTION_DOWN) {
                   if (editTexts[i].text.isEmpty() && i > 0) {
                       editTexts[i - 1].requestFocus()
                       return@setOnKeyListener true
                   }
                }
                false
            }
        }
    }

    private fun startResendTimer(tv: TextView) {
        object : android.os.CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                tv.text = "Resend Code (00:${String.format("%02d", seconds)})"
                tv.isEnabled = false
            }
            override fun onFinish() {
                tv.text = "Resend Code"
                tv.isEnabled = true
                tv.setOnClickListener {
                    startResendTimer(tv)
                    // Logic to resend OTP
                    android.widget.Toast.makeText(this@ForgotPasswordOtpActivity, "Code resent!", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}
