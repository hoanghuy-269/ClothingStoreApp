package com.example.clothingstoreapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val orderId: String,
    val userId: String,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val orderDate: Long,
    var status: String,
    var address : String
) : Parcelable