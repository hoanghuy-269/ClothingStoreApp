package com.example.clothingstoreapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.clothingstoreapp.R
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open, R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainContent, ProductManagerFragment())
                .commit()
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_product -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContent, ProductManagerFragment())
                        .commit()
                }
                R.id.nav_order -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContent, OrderManagerFragment())
                        .commit()
                }
                R.id.nav_users -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContent, FavoriteManagerFragment())
                        .commit()
                }
                R.id.nav_exit -> {
                    AlertDialog.Builder(this)
                        .setTitle("LOGOUT")
                        .setMessage("Nhấn vào CÓ nếu bạn thoát ứng dụng.")
                        .setPositiveButton("CÓ") { _, _ ->
                            FirebaseAuth.getInstance().signOut()
                            Toast.makeText(this, "Đã thoát ứng dụng thành công", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, SignInActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        .setNegativeButton("Không", null)
                        .show()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }


    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
