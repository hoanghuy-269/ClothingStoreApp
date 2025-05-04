package com.example.clothingstoreapp.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.ProductdetailsLayoutBinding
import com.example.clothingstoreapp.databinding.SignInLayoutBinding

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ProductdetailsLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProductdetailsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}