package com.example.clothingstoreapp.Activities

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
        setEvent()
    }
    private fun setEvent()
    {
        binding.btnRight.setOnClickListener {
            val intent = Intent(this, WelcomeOderActivity::class.java)
            startActivity(intent)
        }
        binding.btnleft.setOnClickListener {
            val intent = Intent(this, WelcomeActivity2::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_left)
        }
    }
}