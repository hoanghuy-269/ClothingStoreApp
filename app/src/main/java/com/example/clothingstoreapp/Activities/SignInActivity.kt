package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.HomeLayoutBinding
import com.example.clothingstoreapp.databinding.SignInLayoutBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding:SignInLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SetEvent()
    }

    private fun SetEvent()
    {
        binding.txtSignUp.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
        binding.txtForGotPassWord.setOnClickListener {
            val intent = Intent(this, VerityCodeActivity::class.java)
            startActivity(intent)

            binding.btnSignIn.setOnClickListener {
                val intent = Intent(this, TrangChuActivity::class.java)
                startActivity(intent)
            }
        }
    }
}