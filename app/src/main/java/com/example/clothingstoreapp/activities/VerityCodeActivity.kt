package com.example.clothingstoreapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.VerifyCodeLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class VerityCodeActivity : AppCompatActivity() {
    private lateinit var binding: VerifyCodeLayoutBinding
    private val database = FirebaseDatabase.getInstance().reference
    private val client = OkHttpClient()
    private val apiKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VerifyCodeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ramDomOTP()
    }
    private fun ramDomOTP()
    {
        val email = intent.getStringExtra("email") ?: ""
        binding.txtemail.setText(email)
        binding.btnSend.setOnClickListener {
            val otp = (100000..999999).random().toString()
            saveOtp(email, otp)
            sendOtpToEmail(email, otp)
        }
        binding.btnVerify.setOnClickListener {
            val enteredOtp = getOtpInput()
            if (enteredOtp == null) {
                Toast.makeText(this, "Nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            verifyOtp(email, enteredOtp)
        }
    }
    private fun saveOtp(email: String, otp: String) {
        val userId = email.replace(".", "_")
        database.child("otps").child(userId)
            .setValue(mapOf("otp" to otp, "timestamp" to System.currentTimeMillis()))
    }
    private fun verifyOtp(email: String, otpInput: String) {
        val userId = email.replace(".", "_")
        database.child("otps").child(userId).get().addOnSuccessListener { item ->
            val storedOtp = item.child("otp").getValue(String::class.java)
            if (otpInput == storedOtp) {
                sendResetEmail(email)
            } else {
                Toast.makeText(this, "OTP không đúng", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Lỗi khi kiểm tra OTP", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this, "Đã gửi email đổi mật khẩu", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gửi email thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getOtpInput(): String? {
        val otp = listOf(
            binding.otp1.text, binding.otp2.text, binding.otp3.text,
            binding.otp4.text, binding.otp5.text, binding.otp6.text
        ).joinToString("") { it.toString() }
        return if (otp.length == 6) otp else null
    }

    private fun sendOtpToEmail(email: String, otp: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = JSONObject().apply {
                put("personalizations", JSONArray().put(JSONObject().apply {
                    put("to", JSONArray().put(JSONObject().put("email", email)))
                }))
                put("from", JSONObject().put("email", "maiquocthong212@gmail.com"))
                put("subject", "Mã OTP của bạn")
                put("content", JSONArray().put(JSONObject().apply {
                    put("type", "text/plain")
                    put("value", "Mã OTP của bạn là: $otp")
                }))
            }

            val request = Request.Builder()
                .url("https://api.sendgrid.com/v3/mail/send")
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            try {
                val response = client.newCall(request).execute()
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@VerityCodeActivity, "OTP đã gửi qua email", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@VerityCodeActivity, "Không gửi được OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@VerityCodeActivity, "Lỗi mạng khi gửi OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
