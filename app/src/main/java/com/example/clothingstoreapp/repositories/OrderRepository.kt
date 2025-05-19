package com.example.clothingstoreapp.repositories

import android.util.Log
import com.example.clothingstoreapp.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

object OrderRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ordersCollection = db.collection("orders")

    fun addOrder(order: Order, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onFailure(Exception("User not authenticated"))
            return
        }

        val orderRef = ordersCollection
            .document(userId)
            .collection("userOrders")
            .document(order.orderId)

        val orderWithUserId = order.copy(userId = userId)
        orderRef.set(orderWithUserId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun getAllOrders(onResult: (List<Order>) -> Unit) {
        db.collectionGroup("userOrders")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val orders = querySnapshot.mapNotNull { doc ->
                    try {
                        doc.toObject<Order>().copy(orderId = doc.id)
                    } catch (e: Exception) {
                        Log.e("OrderRepository", "Error parsing order", e)
                        null
                    }
                }
                onResult(orders)
            }
            .addOnFailureListener { e ->
                Log.e("OrderRepository", "Error getting orders", e)
                onResult(emptyList())
            }
    }

    fun updateOrderStatus(orderId: String, userId: String, newStatus: String, onResult: (Boolean) -> Unit) {
        ordersCollection.document(userId)
            .collection("userOrders")
            .document(orderId)
            .update("status", newStatus)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { e ->
                Log.e("OrderRepository", "Error updating status", e)
                onResult(false)
            }
    }

    // Thống nhất sử dụng orderId đơn giản (không chứa userId)
    fun deleteOrder(userId: String, orderId: String, onResult: (Boolean) -> Unit) {
        db.collection("orders")
            .document(userId)
            .collection("userOrders")
            .document(orderId)
            .delete()
            .addOnSuccessListener {
                Log.d("OrderRepository", "Deleted order $orderId for user $userId")
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("OrderRepository", "Error deleting order", e)
                onResult(false)
            }
    }
}