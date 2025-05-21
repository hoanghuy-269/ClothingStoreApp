package com.example.clothingstoreapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Orderdetails(
    val productName: String,
    val size: String,
    val price: Double,
    val quantity: Int,
    val returnPolicy: String,
    val totalPrice: Double,
    val productImage: String,
    val status: String
) : Parcelable
