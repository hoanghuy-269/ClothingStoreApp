package com.example.clothingstoreapp.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val productId: String,
    val image : String,
    val name: String,
    val price: Double,
    val selectedSize: String,
    val quantity: Int,
    var status: String
) : Parcelable