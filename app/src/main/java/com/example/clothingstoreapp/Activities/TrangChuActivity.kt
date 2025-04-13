package com.example.clothingstoreapp.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.TrangchuLayoutBinding

class TrangChuActivity : AppCompatActivity() {
    private lateinit var binding:  TrangchuLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrangchuLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

}