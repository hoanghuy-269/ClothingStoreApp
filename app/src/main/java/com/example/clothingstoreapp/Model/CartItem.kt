package com.example.clothingstoreapp.Model

data class CartItem (
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val selectedSize: String = "",
    val quantity: Int = 1
)
