package com.example.clothingstoreapp.Activities.Welcome

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.WelcomeHomeLayoutBinding

class WelcomeActivity2 : AppCompatActivity() {

    private lateinit var binding: WelcomeHomeLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WelcomeHomeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRight.setOnClickListener {
            val intent = Intent(this,WelcomeActivity3::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)

        }

    }
}