package com.example.clothingstoreapp.repository

import android.util.Log
import com.example.clothingstoreapp.models.Product
import com.google.firebase.firestore.FirebaseFirestore

object ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val defaultSizes = listOf("S", "M", "L", "XL", "XXL", "XXXL")

    // Thêm sản phẩm
    fun addProduct(product: Product, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val productWithSizes = product.copy(sizes = defaultSizes)
        val docRef = db.collection("products").document()

        val productData = hashMapOf(
            "id" to docRef.id,
            "name" to productWithSizes.name,
            "price" to productWithSizes.price,
            "description" to productWithSizes.description,
            "stock" to productWithSizes.stock,
            "categoryId" to productWithSizes.categoryId,
            "images" to productWithSizes.images,
            "createdAt" to productWithSizes.createdAt,
            "rating" to productWithSizes.rating,
            "sizes" to productWithSizes.sizes
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

    // Lấy tất cả sản phẩm
    fun getAllProducts(onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("products")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val productList = mutableListOf<Product>()
                for (document in querySnapshot) {
                    val productData = document.toObject(Product::class.java)
                    val updatedSizes = if (productData.sizes.isNullOrEmpty()) {
                        listOf("S", "M", "L", "XL", "XXL", "XXXL")
                    } else {
                        productData.sizes
                    }
                    val product = productData.copy(id = document.id, sizes = updatedSizes)
                    productList.add(product)
                }
                onSuccess(productList)
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "Lỗi khi lấy sản phẩm", e)
                onFailure(e)
            }
    }

    fun getProductsById(ids: Set<String>, onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        if (ids.isEmpty()) {
            onSuccess(emptyList())
            return
        }

        db.collection("products")
            .whereIn("id", ids.toList())
            .get()
            .addOnSuccessListener { querySnapshot ->
                val products = querySnapshot.map { document ->
                    val productData = document.toObject(Product::class.java)
                    val updatedSizes = if (productData.sizes.isNullOrEmpty()) {
                        listOf("S", "M", "L", "XL", "XXL", "XXXL")
                    } else {
                        productData.sizes
                    }
                    productData.copy(id = document.id, sizes = updatedSizes)
                }
                onSuccess(products)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }


    fun updateProduct(product: Product, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val productWithSizes = product.copy(sizes = product.sizes.ifEmpty { defaultSizes })

        val updateData = mapOf(
            "name" to productWithSizes.name,
            "price" to productWithSizes.price,
            "description" to productWithSizes.description,
            "stock" to productWithSizes.stock,
            "categoryId" to productWithSizes.categoryId,
            "images" to productWithSizes.images,
            "rating" to productWithSizes.rating,
            "sizes" to productWithSizes.sizes
        )

        db.collection("products").document(product.id)
            .update(updateData)
            .addOnSuccessListener {
                Log.d("FIRESTORE", "Cập nhật sản phẩm thành công")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "Lỗi khi cập nhật sản phẩm", e)
                onFailure(e)
            }

    }

    fun deleteProduct(productId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("products").document(productId)
            .delete()
            .addOnSuccessListener {
                Log.d("FIRESTORE", "Xóa sản phẩm thành công")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "Lỗi khi xóa sản phẩm", e)
                onFailure(e)
            }
    }

    fun getProductById(productId: String, onSuccess: (Product) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val product = document.toObject(Product::class.java)?.let { original ->
                        // Tạo bản copy với sizes mặc định nếu cần
                        if (original.sizes.isEmpty()) {
                            original.copy(sizes = defaultSizes)
                        } else {
                            original
                        }.apply { id = document.id }
                    }
                    product?.let { onSuccess(it) } ?: onFailure(Exception("Product data is null"))
                } else {
                    onFailure(Exception("Product not found"))
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}