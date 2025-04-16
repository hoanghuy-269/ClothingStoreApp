package com.example.clothingstoreapp.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.HomeLayoutBinding
import com.example.clothingstoreapp.databinding.MyWishlistLayoutBinding
import com.example.clothingstoreapp.databinding.WelcomeLayoutBinding

class WishlistActivity : AppCompatActivity() {
    private lateinit var binding : MyWishlistLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyWishlistLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}