package com.example.clothingstoreapp.Activities.Welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.Activities.TrangChuActivity
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.WelcomeCartLayoutBinding

class WelcomeActivity4 : AppCompatActivity() {
    private lateinit var binding: WelcomeCartLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WelcomeCartLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRight.setOnClickListener {
            val intent = Intent(this,TrangChuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
        binding.btnleft.setOnClickListener {
            val intent = Intent(this,WelcomeActivity3::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_left)

        }

    }
}