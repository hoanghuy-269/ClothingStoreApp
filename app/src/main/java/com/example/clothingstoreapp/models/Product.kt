package com.example.clothingstoreapp.models

import com.google.firebase.Timestamp

data class Product(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val categoryId: String = "",
    val images: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val rating: Float = 0f,
    val sizes: List<String> = listOf("S", "M", "L", "XL", "XXL", "XXXL")
)
