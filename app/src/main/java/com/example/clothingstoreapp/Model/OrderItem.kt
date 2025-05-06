package com.example.clothingstoreapp.Model

data class OrderItem(
    val productId: String = "",
    var quantity: Int = 1,
    val price: Double = 0.0
)

data class Order(
    val id: String = "",
    val userId: String = "",
    val orderDate: String = "",
    val status: String = "",
    val totalAmount: Double = 0.0,
    val items: List<OrderItem> = emptyList()
)