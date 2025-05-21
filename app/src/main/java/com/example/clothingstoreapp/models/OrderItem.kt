package com.example.clothingstoreapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val productId: String = "",
    val image: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val selectedSize: String = "",
    val quantity: Int = 0,
    var status: String = ""
) : Parcelable {
    // Empty constructor for Firestore
    constructor() : this("", "", "", 0.0, "", 0, "")
}