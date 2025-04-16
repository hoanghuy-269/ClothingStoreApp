package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.HomeLayoutBinding

class HomePageActivity : AppCompatActivity() {
    private lateinit var binding : HomeLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun setEvent(){
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Xử lý chuyển đến trang Home
                    true
                }
                R.id.nav_cart -> {
                    // Xử lý chuyển đến trang Wishlist
                    true
                }
                R.id.nav_favorite -> {
                    val intent = Intent(this, WishlistActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    true
                }
                R.id.nav_chat -> {
                    // Xử lý chuyển đến trang Account
                    true
                }
                R.id.nav_profile -> {
                    // Xử lý chuyển đến trang Account
                    true
                }
                else -> false
            }
        }

    }
}