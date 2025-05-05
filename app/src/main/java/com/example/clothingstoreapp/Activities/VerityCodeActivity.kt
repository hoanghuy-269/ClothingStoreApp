package com.example.clothingstoreapp.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.VerifyCodeLayoutBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import java.util.concurrent.TimeUnit

class VerityCodeActivity : AppCompatActivity() {
    private lateinit var binding: VerifyCodeLayoutBinding
    private val SMS_PERMISSION_CODE = 1001
    private lateinit var mAuth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VerifyCodeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        val phoneNumber = intent.getStringExtra("Phone_number") ?: ""
        binding.txtPhone.setText(phoneNumber)
        val email = intent.getStringExtra("email") ?: ""
        binding.txtemail.setText(email)

        setEventListeners()
        checkAndRequestSmsPermission()
    }

    private fun setEventListeners() {
        // Gửi mã OTP khi nhấn "Send"
        binding.btnSend.setOnClickListener {
            val phoneNumber = binding.txtPhone.text.toString()
            if (phoneNumber.isNotEmpty()) {
                sendOtpToPhone(phoneNumber)
            } else {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
            }
        }

        // Xác minh OTP khi nhấn "Verify"
        binding.btnVerify.setOnClickListener {
            val otp = getOtpFromEditText()
            if (otp != null) {
                verifyOtp(otp)
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ mã OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndRequestSmsPermission() {
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS
                ),
                SMS_PERMISSION_CODE
            )
        }
    }

    private fun sendOtpToPhone(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)       // Số điện thoại của người dùng
            .setTimeout(60L, TimeUnit.SECONDS)  // Thời gian chờ (60 giây)
            .setActivity(this)                  // Context của Activity
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    // Xác thực thành công mà không cần OTP
                    signInWithPhoneAuthCredential(phoneAuthCredential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Xử lý nếu xác minh OTP thất bại
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this@VerityCodeActivity, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@VerityCodeActivity, "Lỗi xác thực: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("OTP", "Verification failed: ${e.message}")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // Lưu lại verificationId khi mã OTP được gửi
                    this@VerityCodeActivity.verificationId = verificationId
                    Toast.makeText(this@VerityCodeActivity, "Mã OTP đã được gửi", Toast.LENGTH_SHORT).show()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtp(otp: String) {
        val verificationId = verificationId ?: return
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val email = intent.getStringExtra("email") ?: ""
                    val phone = intent.getStringExtra("Phone_number") ?: ""

                    // Gửi email đặt lại mật khẩu
                    sendPasswordResetEmail(email, phone)
                } else {
                    Toast.makeText(this, "Lỗi xác thực OTP", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendPasswordResetEmail(email: String, phone: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { resetTask ->
                if (resetTask.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Đã gửi email đặt lại mật khẩu đến $email",
                        Toast.LENGTH_LONG
                    ).show()

                    // Quay về màn hình đăng nhập hoặc trang chính
                    val intent = Intent(this, SignInActivity::class.java).apply {
                        putExtra("email", email)
                        putExtra("phone", phone)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    val errorMsg = resetTask.exception?.message ?: "Gửi email thất bại"
                    Toast.makeText(this, "Lỗi: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getOtpFromEditText(): String? {
        val otp = StringBuilder()
        otp.append(binding.otp1.text.toString())
        otp.append(binding.otp2.text.toString())
        otp.append(binding.otp3.text.toString())
        otp.append(binding.otp4.text.toString())
        otp.append(binding.otp5.text.toString())
        otp.append(binding.otp6.text.toString())
        return if (otp.length == 6) otp.toString() else null
    }
}
