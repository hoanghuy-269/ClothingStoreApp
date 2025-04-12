package com.example.clothingstoreapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.TrangChuLayoutBinding

class TrangChuActivity : AppCompatActivity() {
    private lateinit var binding: TrangChuLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrangChuLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}