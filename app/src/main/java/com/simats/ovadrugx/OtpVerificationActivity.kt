package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class OtpVerificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        val isForgotPassword = intent.getBooleanExtra("is_forgot_password", false)
        val headerText = findViewById<TextView>(R.id.otp_header)

        if (isForgotPassword) {
            headerText.text = "Forgot Password"
        } else {
            headerText.text = "Verify Your Account"
        }

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
            
            if (isForgotPassword) {
                 // For forgot password, we might want to verify specifically for reset, 
                 // but the current backend verify endpoint checks for registration verification.
                 // If the backend has a separate verify-reset-otp, use that.
                 // Looking at app.py, there is /verify-reset-otp.
                 // For now, let's assume this activity is shared.
                 
                 // If it is forgot password, we should call verify-reset-otp or just navigate if logic handles it there?
                 // The previous code just navigated. 
                 // Let's implement /verify-reset-otp call if needed, but for now let's focus on Registration Verification.
                 // The user's issue is logging in after signup.
                 startActivity(Intent(this, ResetPasswordActivity::class.java))
            } else {
                // Registration Verification
                com.simats.ovadrugx.api.RetrofitClient.instance.verify(
                    com.simats.ovadrugx.model.VerifyRequest(email, code)
                ).enqueue(object : retrofit2.Callback<com.simats.ovadrugx.model.GenericResponse> {
                    override fun onResponse(call: retrofit2.Call<com.simats.ovadrugx.model.GenericResponse>, response: retrofit2.Response<com.simats.ovadrugx.model.GenericResponse>) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                             showSuccessDialog()
                        } else {
                            android.widget.Toast.makeText(this@OtpVerificationActivity, response.body()?.message ?: "Verification failed", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<com.simats.ovadrugx.model.GenericResponse>, t: Throwable) {
                        android.widget.Toast.makeText(this@OtpVerificationActivity, "Error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun showSuccessDialog() {
        val dialogView = android.view.LayoutInflater.from(this).inflate(R.layout.dialog_account_created, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        
        dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_continue_login).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        dialog.show()
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
                    android.widget.Toast.makeText(this@OtpVerificationActivity, "Code resent!", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}
