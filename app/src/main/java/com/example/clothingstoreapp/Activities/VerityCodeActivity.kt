package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.VerifyCodeLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class VerityCodeActivity : AppCompatActivity() {
    private lateinit var binding: VerifyCodeLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VerifyCodeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy email từ intent
        val email = intent.getStringExtra("email") ?: ""
        binding.txtemail.setText(email)

        setEventListeners()
    }

    private fun setEventListeners() {
        // Gửi mã OTP khi nhấn "Send"
        binding.btnSend.setOnClickListener {
            val email = binding.txtemail.text.toString().trim()
            if (email.isNotEmpty()) {
                // Tạo và gửi OTP
                val otp = (100000..999999).random().toString()
                saveOtpToFirebase(email, otp)
                SendOtpEmailTask().execute(email, otp)
            } else {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
            }
        }

        // Xác minh OTP khi nhấn "Verify"
        binding.btnVerify.setOnClickListener {
            val otp = getOtpFromEditText()
            if (otp != null) {
                verifyOtp(binding.txtemail.text.toString().trim(), otp)
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ mã OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Lưu OTP vào Firebase
    private fun saveOtpToFirebase(email: String, otp: String) {
        val database = FirebaseDatabase.getInstance().getReference("otps")
        val userId = email.replace(".", "_")
        database.child(userId).setValue(mapOf("otp" to otp, "timestamp" to System.currentTimeMillis()))
            .addOnSuccessListener {
                Toast.makeText(this, "OTP đã được lưu", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi lưu OTP", Toast.LENGTH_SHORT).show()
            }
    }

    // Xác minh OTP
    private fun verifyOtp(email: String, inputOtp: String) {
        val userId = email.replace(".", "_")
        val database = FirebaseDatabase.getInstance().getReference("otps").child(userId)
        database.get().addOnSuccessListener { snapshot ->
            val storedOtp = snapshot.child("otp").getValue(String::class.java)
            if (inputOtp == storedOtp) {
                Toast.makeText(this, "Xác minh OTP thành công!", Toast.LENGTH_SHORT).show()
                // Gửi email đặt lại mật khẩu
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(this, "OTP không đúng", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Lỗi khi kiểm tra OTP", Toast.LENGTH_SHORT).show()
        }
    }

    // Gửi email đặt lại mật khẩu qua Firebase
    private fun sendPasswordResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { resetTask ->
                if (resetTask.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Đã gửi email đặt lại mật khẩu đến $email",
                        Toast.LENGTH_LONG
                    ).show()
                    // Quay về màn hình đăng nhập
                    val intent = Intent(this, SignInActivity::class.java).apply {
                        putExtra("email", email)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    val errorMsg = resetTask.exception?.message ?: "Gửi email thất bại"
                    Toast.makeText(this, "Lỗi: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Lấy OTP từ các EditText
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

    // AsyncTask để gửi OTP qua SendGrid
    private inner class SendOtpEmailTask : AsyncTask<String, Void, Boolean>() {
        override fun doInBackground(vararg params: String): Boolean {
            val email = params[0]
            val otp = params[1]
            val apiKey = ""
            val client = OkHttpClient()

            val json = JSONObject().apply {
                put("personalizations", JSONArray().put(JSONObject().apply {
                    put("to", JSONArray().put(JSONObject().put("email", email)))
                }))
                put("from", JSONObject().put("email", "maiquocthong212@gmail.com"))
                put("subject", "Mã OTP của bạn")
                put("content", JSONArray().put(JSONObject().apply {
                    put("type", "text/plain")
                    put("value", "Mã OTP của bạn là: $otp. Vui lòng nhập mã này để xác minh.")
                }))
            }

            val request = Request.Builder()
                .url("https://api.sendgrid.com/v3/mail/send")
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            return try {
                val response = client.newCall(request).execute()
                response.isSuccessful
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(result: Boolean) {
            if (result) {
                Toast.makeText(this@VerityCodeActivity, "OTP đã được gửi qua email", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@VerityCodeActivity, "Lỗi khi gửi OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
}