package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.HomeLayoutBinding
import com.example.clothingstoreapp.databinding.SignInLayoutBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding:SignInLayoutBinding
    private lateinit var mAuth :FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SetEvent()
        mAuth = FirebaseAuth.getInstance()
    }

    private fun SetEvent()
    {
        binding.btnSignIn.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            {
                Toast.makeText(this,"Vui lòng không được để trống ",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task->
                    if(task.isSuccessful)
                    {
                        Toast.makeText(this," Đăng Nhập thành công",Toast.LENGTH_LONG).show()
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(this," Vui Long kiểm tra lại tài khoản và mật khẩu",Toast.LENGTH_LONG).show()
                    }
                }
        }
        binding.txtSignUp.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
        binding.txtForgotPassword.setOnClickListener {
            val intent = Intent(this,VerityCodeActivity::class.java)
            startActivity(intent)
        }
    }


}