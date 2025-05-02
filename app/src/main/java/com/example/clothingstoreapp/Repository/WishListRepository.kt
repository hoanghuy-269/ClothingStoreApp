package com.example.clothingstoreapp.repository

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
}