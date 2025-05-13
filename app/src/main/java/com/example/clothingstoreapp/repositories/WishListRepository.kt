package com.example.clothingstoreapp.repositories

import com.example.clothingstoreapp.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object WishListRepository {

    private val db = FirebaseFirestore.getInstance()

    fun addFavorite(productId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val favoriteRef = db.collection("favorites").document(userId)
            favoriteRef.update("favoriteProducts", FieldValue.arrayUnion(productId))
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e ->
                    favoriteRef.set(mapOf("favoriteProducts" to listOf(productId)))
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it) }
                }
        } else {
            onFailure(Exception("User not logged in"))
        }
    }

    fun removeFavorite(productId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val favoriteRef = db.collection("favorites").document(userId)
            favoriteRef.update("favoriteProducts", FieldValue.arrayRemove(productId))
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        } else {
            onFailure(Exception("User not logged in"))
        }
    }

    fun getFavoriteProductIds(onSuccess: (Set<String>) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val favoriteRef = db.collection("favorites").document(userId)
            favoriteRef.get()
                .addOnSuccessListener { document ->
                    val favoriteIds = document.get("favoriteProducts") as? List<String> ?: emptyList()
                    onSuccess(favoriteIds.toSet())
                }
                .addOnFailureListener { onFailure(it) }
        } else {
            onFailure(Exception("User not logged in"))
        }
    }

    fun getFavoriteProductsByCategory(categoryId: String, onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val favoriteRef = db.collection("favorites").document(userId)
            favoriteRef.get()
                .addOnSuccessListener { document ->
                    val favoriteIds = document.get("favoriteProducts") as? List<String> ?: emptyList()
                    if (favoriteIds.isNotEmpty()) {
                        db.collection("products")
                            .whereIn(FieldPath.documentId(), favoriteIds)
                            .whereEqualTo("categoryId", categoryId)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val productList = querySnapshot.toObjects(Product::class.java)
                                onSuccess(productList)
                            }
                            .addOnFailureListener { onFailure(it) }
                    } else {
                        onSuccess(emptyList())
                    }
                }
                .addOnFailureListener { onFailure(it) }
        } else {
            onFailure(Exception("User not logged in"))
        }
    }
}
