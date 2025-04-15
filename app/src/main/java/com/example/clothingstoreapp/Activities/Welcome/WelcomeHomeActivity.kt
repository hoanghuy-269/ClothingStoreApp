package com.example.clothingstoreapp.Activities.Welcome

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.WelcomeLayoutBinding

class WelcomeHomeActivity : AppCompatActivity() {
    private lateinit var binding: WelcomeLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WelcomeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnWelcomeGetarted.setOnClickListener {
            val intent = Intent(this,WelcomeActivity2::class.java)
            startActivity(intent)
        }
    }
}