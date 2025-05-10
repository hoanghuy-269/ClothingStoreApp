package com.example.clothingstoreapp.Model

data class Cart(
    val userId: String = "",
    val createdDate: String = "",
    val items: List<CartItem> = emptyList()
)
