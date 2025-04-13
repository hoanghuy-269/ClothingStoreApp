package com.example.clothingstoreapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.WelcomeAppLayoutBinding

class TrangChuActivity : AppCompatActivity() {
    private lateinit var binding: WelcomeAppLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WelcomeAppLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}