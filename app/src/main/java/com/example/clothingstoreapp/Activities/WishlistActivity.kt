package com.example.clothingstoreapp.Activities

import android.content.Intent
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
        binding.bottomNavigation.selectedItemId = R.id.nav_favorite

        setEvent()
    }
    private fun setEvent() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomePageActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_cart -> {
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_favorite -> {
                    true
                }

                R.id.nav_chat -> {
                    val intent = Intent(this, WishlistActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}