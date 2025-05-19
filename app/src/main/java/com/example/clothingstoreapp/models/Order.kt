package com.example.clothingstoreapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val orderId: String = "", // Giá trị mặc định để đảm bảo không null
    val userId: String? = null, // Nullable vì userId có thể thiếu
    val items: List<OrderItem> = emptyList(), // Giá trị mặc định
    val totalPrice: Double = 0.0,
    val orderDate: Long = 0L,
    var status: String? = null, // Nullable để xử lý giá trị null từ Firestore
    var address: String? = null, // Nullable
    var deliveryDate: Long = 0L
) : Parcelable{
    constructor() : this("", null, emptyList(), 0.0, 0L, null, null, 0L)
}