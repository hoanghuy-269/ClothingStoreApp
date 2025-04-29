package com.example.clothingstoreapp.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.MainLayoutBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hiển thị Fragment mặc định
        replaceFragment(HomeActivity())

        // Xử lý khi chọn bottom nav item
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> replaceFragment(HomeActivity())
                R.id.nav_cart -> replaceFragment(CartFragment())
                R.id.nav_favorite -> replaceFragment(WishListFragment())
                R.id.nav_chat -> replaceFragment(ChatFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())

            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}
