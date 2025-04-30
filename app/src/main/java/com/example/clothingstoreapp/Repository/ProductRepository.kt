package com.example.clothingstoreapp.repository

import android.util.Log
import com.example.clothingstoreapp.model.Product
import com.google.firebase.firestore.FirebaseFirestore

object ProductRepository {
    private val db = FirebaseFirestore.getInstance()

    // Phương thức thêm sản phẩm vào Firestore
    fun addProduct(product: Product, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val docRef = db.collection("products").document() // tạo document có id
        val productData = mapOf(
            "id" to docRef.id,  // gán id vào document
            "name" to product.name,
            "price" to product.price,
            "description" to product.description,
            "stock" to product.stock,
            "categoryId" to product.categoryId,
            "images" to product.images,
            "createdAt" to product.createdAt,
            "rating" to product.rating
        )

        docRef.set(productData)
            .addOnSuccessListener {
                Log.d("FIRESTORE", "Thêm sản phẩm thành công với ID: ${docRef.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "Lỗi khi thêm sản phẩm", e)
                onFailure(e)
            }
    }


    // Phương thức lấy tất cả sản phẩm từ Firestore
    fun getAllProducts(onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("products")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Chuyển dữ liệu Firestore thành danh sách các đối tượng Product
                val productList = mutableListOf<Product>()
                for (document in querySnapshot) {
                    val product = document.toObject(Product::class.java)
                    product.id = document.id  // gán id từ Firestore vào model
                    productList.add(product)
                }
                // Gọi onSuccess với danh sách các sản phẩm
                onSuccess(productList)
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "Lỗi khi lấy sản phẩm", e)
                onFailure(e)
            }
    }
}
