package com.example.clothingstoreapp.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.CartLayoutBinding
import com.example.clothingstoreapp.databinding.HomeLayoutBinding

class TrangChuActivity : AppCompatActivity() {
    private lateinit var binding:  CartLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CartLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}