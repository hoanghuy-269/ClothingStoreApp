package com.example.clothingstoreapp.Activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.SignInLayoutBinding
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: SignInLayoutBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SignInLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance() // Khởi tạo sớm
        callbackManager = CallbackManager.Factory.create()
        setupEvents()

        // Nhận dữ liệu từ intent (nếu có)
        intent.getStringExtra("email")?.let { binding.edtEmail.setText(it) }
        intent.getStringExtra("phone")?.let { binding.txtPhone.setText(it) }
    }

    private fun setupEvents() {
        binding.btnSignIn.setOnClickListener {

            val phone = binding.txtPhone.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            if (phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng không để trống", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            binding.progressBar.visibility = View.VISIBLE

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish() // Đóng màn hình đăng nhập
                    } else {
                        Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.txtSignUp.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }

        binding.txtForgotPassword.setOnClickListener {
            val phone = binding.txtPhone.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()

            if (phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại và email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, VerityCodeActivity::class.java)
            intent.putExtra("Phone_number", phone)
            intent.putExtra("email", email)
            startActivity(intent)
        }
    }
}
