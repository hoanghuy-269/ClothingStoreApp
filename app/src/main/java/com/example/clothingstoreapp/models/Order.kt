package com.example.clothingstoreapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val orderDate: Long = 0L,
    var status: String = "",
    var address: String = "",
    var deliveryDate: Long = 0L
) : Parcelable {
    // Secondary constructor for Firestore compatibility
    constructor() : this("", "", emptyList(), 0.0, 0L, "", "", 0L)
}