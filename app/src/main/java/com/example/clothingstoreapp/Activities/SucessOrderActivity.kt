package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.OrderLayoutBinding
import com.example.clothingstoreapp.databinding.SucessLayoutBinding

class SucessOrderActivity : AppCompatActivity() {
    private lateinit var binding: SucessLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SucessLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonViewReceipt.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}