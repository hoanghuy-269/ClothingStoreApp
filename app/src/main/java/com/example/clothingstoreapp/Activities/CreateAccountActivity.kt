package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.CreateAccoutLayoutBinding

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: CreateAccoutLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateAccoutLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SetEvent()
    }
    private fun SetEvent()
    {
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.txtSignIn.setOnClickListener {
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

    }
}