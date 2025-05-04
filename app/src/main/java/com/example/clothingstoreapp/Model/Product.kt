package com.example.clothingstoreapp.model

import com.google.firebase.Timestamp

data class Product(
    var id: String = "",  // thêm dòng này
    val name: String = "",
    val description: String = "",
    val price: Int = 0,
    val stock: Int = 0,
    val categoryId: String = "",
    val images: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val rating: Float = 0f,
    val sizes: List<String>? = null // Đảm bảo tham số này có giá trị mặc định là null
) {
    // Constructor không đối số cần thiết cho Firestore
    constructor() : this("", "", "", 0, 0, "", "", Timestamp.now(), 0f, null)
}

