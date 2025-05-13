package com.example.clothingstoreapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Orderdetails(
    val productName: String,  // Tên sản phẩm
    val size: String,         // Kích thước sản phẩm
    val price: Double,        // Giá sản phẩm
    val quantity: Int,        // Số lượng sản phẩm
    val returnPolicy: String, // Chính sách hoàn trả
    val totalPrice: Double,   // Giá trị tổng cộng
    val productImage: String, // Hình ảnh sản phẩm
    val status: String        // Trạng thái của đơn hàng (Ví dụ: "Chờ vận chuyển", "Đang vận chuyển", "Đã vận chuyển")
) : Parcelable
