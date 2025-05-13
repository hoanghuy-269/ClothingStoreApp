package com.example.clothingstoreapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.SucessLayoutBinding

class SucessOrderActivity : AppCompatActivity() {
    private lateinit var binding: SucessLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SucessLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonViewOrder.setOnClickListener {
            startActivity(Intent(this, OrderdetailsActivity::class.java))
        }
        binding.buttonViewReceipt.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}