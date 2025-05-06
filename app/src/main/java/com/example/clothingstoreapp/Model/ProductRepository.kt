package com.example.clothingstoreapp.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getProductById(productId: String, onSuccess: (Product) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val product = document.toObject(Product::class.java)
                    product?.let { onSuccess(it) } ?: onFailure(Exception("Sản phẩm không tồn tại"))
                } else {
                    onFailure(Exception("Không tìm thấy sản phẩm"))
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
