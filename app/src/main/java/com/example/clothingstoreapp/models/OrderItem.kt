package com.example.clothingstoreapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val productId: String? = null,  // Nullable với giá trị mặc định null
    val image: String? = null,      // Nullable với giá trị mặc định null
    val name: String? = null,       // Nullable với giá trị mặc định null
    val price: Double = 0.0,        // Giá trị mặc định 0.0
    val selectedSize: String? = null, // Nullable với giá trị mặc định null
    val quantity: Int = 0,          // Giá trị mặc định 0
    var status: String? = null      // Nullable với giá trị mặc định null
) : Parcelable {
    // Constructor không tham số cho Firestore
    constructor() : this(null, null, null, 0.0, null, 0, null)
}