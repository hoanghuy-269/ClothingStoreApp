package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.WelcomeOderLayoutBinding

class WelcomeOderActivity : AppCompatActivity() {
    private lateinit var binding: WelcomeOderLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WelcomeOderLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRight.setOnClickListener {
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }
        binding.btnleft.setOnClickListener {
            val intent = Intent(this, WelcomeActivity3::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_left)

        }

    }
}