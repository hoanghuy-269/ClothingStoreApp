package com.example.clothingstoreapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val productId: String = "",          // Non-null with empty default
    val image: String = "",              // Non-null with empty default
    val name: String = "",               // Non-null with empty default
    val price: Double = 0.0,             // Default value 0.0
    val selectedSize: String = "",       // Non-null with empty default
    val quantity: Int = 0,               // Default value 0
    var status: String = ""              // Non-null with empty default
) : Parcelable {
    // Empty constructor for Firestore
    constructor() : this("", "", "", 0.0, "", 0, "")
}