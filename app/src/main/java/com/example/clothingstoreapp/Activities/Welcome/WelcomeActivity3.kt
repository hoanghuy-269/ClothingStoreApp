package com.example.clothingstoreapp.Activities.Welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.WelcomeCategoryLayoutBinding

class WelcomeActivity3 : AppCompatActivity() {
    private lateinit var binding: WelcomeCategoryLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WelcomeCategoryLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRight.setOnClickListener {
            val intent = Intent(this,WelcomeActivity4::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
        binding.btnleft.setOnClickListener {
            val intent = Intent(this,WelcomeActivity2::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_left)

        }

    }
}