package com.example.clothingstoreapp.repository

import com.example.clothingstoreapp.model.Product
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object WishListRepository {
    fun addFavorite(userId: String, productId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userFavoritesRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        userFavoritesRef.update("favoriteProducts", FieldValue.arrayUnion(productId))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun removeFavorite(userId: String, productId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userFavoritesRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        userFavoritesRef.update("favoriteProducts", FieldValue.arrayRemove(productId))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getFavoriteProductIds(userId: String, onSuccess: (Set<String>) -> Unit, onFailure: (Exception) -> Unit) {
        val userFavoritesRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        userFavoritesRef.get()
            .addOnSuccessListener { document ->
                val favoriteIds = document.get("favoriteProducts") as? List<String> ?: emptyList()
                onSuccess(favoriteIds.toSet())
            }
            .addOnFailureListener { onFailure(it) }
    }
    fun getFavoriteProductsByCategory(userId: String, categoryId: String, onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        val userFavoritesRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        userFavoritesRef.get()
            .addOnSuccessListener { document ->
                val favoriteIds = document.get("favoriteProducts") as? List<String> ?: emptyList()
                if (favoriteIds.isNotEmpty()) {
                    val productsRef = FirebaseFirestore.getInstance().collection("products")
                    productsRef.whereIn(FieldPath.documentId(), favoriteIds)
                        .whereEqualTo("categoryId", categoryId)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val productList = querySnapshot.toObjects(Product::class.java)
                            onSuccess(productList)
                        }
                        .addOnFailureListener { e ->
                            onFailure(e)
                        }
                } else {
                    onSuccess(emptyList())
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}