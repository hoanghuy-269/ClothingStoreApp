package com.example.clothingstoreapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.WelcomeLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WelcomeScreenActivity : AppCompatActivity() {
    private lateinit var binding: WelcomeLayoutBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db :FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WelcomeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mAuth = FirebaseAuth.getInstance()
        // Kiểm tra nếu người dùng đã đăng nhập
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role")
                        when (role) {
                            "shipper" -> {
                                startActivity(Intent(this, ShipperActivity::class.java))
                            }
                            "admin" -> {
//                                startActivity(Intent(this, MainActivity::class.java))
                            }
                            else -> {
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                        }
                        finish()
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Lỗi khi lấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show()
                }
        }

        setEvent()
    }
    private fun setEvent()
    {
        binding.btnWelcomeGetarted.setOnClickListener {
            val intent = Intent(this, OnboardingScreenActivity::class.java)
            startActivity(intent)
        }
        binding.txtWelcomeSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

}