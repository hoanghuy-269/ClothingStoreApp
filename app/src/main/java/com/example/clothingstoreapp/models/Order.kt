package com.example.clothingstoreapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    var orderId: String = "",
    var userId: String = "",
    val items: List<OrderItem> = listOf(),
    val totalPrice: Double = 0.0,
    val orderDate: Long = 0L,
    var status: String = "",
    var address : String = ""
) : Parcelable