package com.example.clothingstoreapp.Model

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    var quantity: Int = 1,
    val price: Double = 0.0,
    val size: String = ""
)

data class Order(
    val userId: String = "",
    val orderDate: String = "",
    val status: String = "",
    val totalAmount: Double = 0.0,
    val items: List<OrderItem> = emptyList()
)
