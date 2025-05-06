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
        if (intent.getBooleanExtra("SHOW_CART_FRAGMENT", false)) {
            replaceFragment(CartActivity()) // Hiển thị CartFragment nếu cần
        } else {
            replaceFragment(HomeActivity()) // Hiển thị HomeFragment nếu không
        }
        // Xử lý khi chọn bottom nav item
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> replaceFragment(HomeActivity())
                R.id.nav_cart -> replaceFragment(CartActivity())
                R.id.nav_favorite -> replaceFragment(WishListActivity())
                R.id.nav_chat -> replaceFragment(ChatActivity())
                R.id.nav_profile -> replaceFragment(ProfileActivity())

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
