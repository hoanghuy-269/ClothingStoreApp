package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.WelcomeHomeLayoutBinding

class WelcomeActivity2 : AppCompatActivity() {

    private lateinit var binding: WelcomeHomeLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WelcomeHomeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SetonClick()

    }
    private fun SetonClick()
    {
        binding.btnRight.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

    }
}