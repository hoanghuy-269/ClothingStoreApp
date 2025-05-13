package com.example.clothingstoreapp.repositories

import android.util.Log
import com.example.clothingstoreapp.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

object OrderRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ordersCollection = db.collection("orders")

    fun addOrder(order: Order, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            onFailure(Exception("User not authenticated"))
            return
        }

        val orderRef = FirebaseFirestore.getInstance()
            .collection("orders")
            .document(userId)
            .collection("userOrders")
            .document(order.orderId)

        // Thêm userId vào order
        val orderWithUserId = order.copy(userId = userId)

        orderRef.set(orderWithUserId)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

//    fun getOrder(orderId: String, onResult: (Order?) -> Unit) {
//        ordersCollection.document(orderId).get()
//            .addOnSuccessListener { snapshot ->
//                val order = snapshot.toObject<Order>()?.copy(id = orderId) // Gán ID tài liệu
//                onResult(order)
//            }
//            .addOnFailureListener { e ->
//                Log.e("OrderRepository", "Lỗi khi lấy hóa đơn: $orderId", e)
//                onResult(null)
//            }
//    }

    fun getAllOrders(onResult: (List<Order>) -> Unit) {
        ordersCollection.get()
            .addOnSuccessListener { result ->
                val orders = result.mapNotNull { it.toObject<Order>() }
                onResult(orders)
            }
            .addOnFailureListener { e ->
                Log.e("OrderRepository", "Lỗi khi lấy danh sách hóa đơn", e)
                onResult(emptyList())
            }
    }

    fun updateOrder(orderId: String, updatedOrder: Order, onResult: (Boolean) -> Unit) {
        ordersCollection.document(orderId).set(updatedOrder)
            .addOnSuccessListener {
                Log.d("OrderRepository", "Cập nhật hóa đơn thành công: $orderId")
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("OrderRepository", "Lỗi khi cập nhật hóa đơn", e)
                onResult(false)
            }
    }

    fun deleteOrder(orderId: String, onResult: (Boolean) -> Unit) {
        ordersCollection.document(orderId).delete()
            .addOnSuccessListener {
                Log.d("OrderRepository", "Xóa hóa đơn thành công: $orderId")
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("OrderRepository", "Lỗi khi xóa hóa đơn", e)
                onResult(false)
            }
    }
}
