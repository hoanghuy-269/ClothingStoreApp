package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.CreateAccoutLayoutBinding
import com.example.clothingstoreapp.databinding.NewPasswordLayoutBinding
import com.example.clothingstoreapp.databinding.SignInLayoutBinding

class NewPasswordActivity : AppCompatActivity() {
    private lateinit var binding: NewPasswordLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewPasswordLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SetEvent()
    }
    private fun SetEvent()
    {
        binding.btnCreateNewPassword.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}