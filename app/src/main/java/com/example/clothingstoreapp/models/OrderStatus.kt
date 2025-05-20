package com.example.clothingstoreapp.models

data class OrderStatus(
    val statusTitle: String,
    val statusTime: String,
    val statusIconResId: Int,
    val isCurrent: Boolean = false
)
