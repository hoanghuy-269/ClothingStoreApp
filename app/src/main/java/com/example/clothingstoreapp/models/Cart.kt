package com.example.clothingstoreapp.models

data class Cart(
    val userId: String = "",
    val createdDate: String = "",
    val items: List<CartItem> = emptyList()
)
