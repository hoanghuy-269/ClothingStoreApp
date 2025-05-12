package com.example.clothingstoreapp.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.OrderAdapter
import com.example.clothingstoreapp.Model.OrderItem
import com.example.clothingstoreapp.databinding.ActivityOrderdetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderdetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderdetailsBinding
    private lateinit var firestore: FirebaseFirestore
    private var allOrdersList = mutableListOf<OrderItem>()
    private var filteredOrdersList = mutableListOf<OrderItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            super.onBackPressed()
        }
        firestore = FirebaseFirestore.getInstance()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            fetchOrderDetails(userId)
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng.", Toast.LENGTH_SHORT).show()
        }

        setupStatusButtons()
    }

    private fun setupStatusButtons() {
        binding.btnAll.setOnClickListener { filterOrdersByStatus("All") }
        binding.btnPending.setOnClickListener { filterOrdersByStatus("Pending") }
        binding.btnShipping.setOnClickListener { filterOrdersByStatus("Shipping") }
        binding.btnCompleted.setOnClickListener { filterOrdersByStatus("Completed") }
        binding.btnCancelled.setOnClickListener { filterOrdersByStatus("Cancelled") }
    }

    private fun filterOrdersByStatus(status: String) {
        filteredOrdersList.clear()
        filteredOrdersList.addAll(
            when (status) {
                "Pending" -> allOrdersList.filter { it.status == "Pending" }
                "Shipping" -> allOrdersList.filter { it.status == "Shipping" }
                "Completed" -> allOrdersList.filter { it.status == "Completed" }
                "Cancelled" -> allOrdersList.filter { it.status == "Cancelled" }
                else -> allOrdersList
            }
        )
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        if (filteredOrdersList.isEmpty()) {
            binding.recyclerViewOrders.visibility = View.GONE
            binding.noOrdersMessage.visibility = View.VISIBLE
        } else {
            binding.recyclerViewOrders.visibility = View.VISIBLE
            binding.noOrdersMessage.visibility = View.GONE
            val orderAdapter = OrderAdapter(filteredOrdersList)
            binding.recyclerViewOrders.layoutManager = LinearLayoutManager(this)
            binding.recyclerViewOrders.adapter = orderAdapter
        }
    }

    private fun fetchOrderDetails(userId: String) {
        firestore.collection("orders")
            .document(userId)
            .collection("userOrders")
            .get()
            .addOnSuccessListener { documents ->
                allOrdersList.clear()
                if (documents.isEmpty) {
                    binding.noOrdersMessage.visibility = View.VISIBLE
                } else {
                    for (document in documents) {
                        val orderItems = document.get("items")
                        if (orderItems is List<*>) {
                            for (rawItem in orderItems) {
                                if (rawItem is Map<*, *>) {
                                    try {
                                        val productId = rawItem["productId"] as? String ?: ""
                                        val image = rawItem["image"] as? String ?: ""
                                        val name = rawItem["name"] as? String ?: ""
                                        val price = (rawItem["price"] as? Number)?.toDouble() ?: 0.0
                                        val quantity = (rawItem["quantity"] as? Number)?.toInt() ?: 0
                                        val selectedSize = rawItem["selectedSize"] as? String ?: "M"
                                        val status = rawItem["status"] as? String ?: ""

                                        val order = OrderItem(
                                            productId = productId,
                                            image = image,
                                            name = name,
                                            price = price,
                                            selectedSize = selectedSize,
                                            quantity = quantity,
                                            status = status
                                        )

                                        allOrdersList.add(order)
                                        Log.d("OrderAdded", order.toString())
                                    } catch (e: Exception) {
                                        Log.e("ParseError", "Lỗi chuyển đổi đơn hàng: ${e.message}")
                                    }
                                }
                            }
                        } else {
                            Log.e("DataError", "Dữ liệu 'items' không đúng định dạng: $orderItems")
                        }
                    }
                    filterOrdersByStatus("All") // Hiển thị tất cả khi load xong
                }
            }
            .addOnFailureListener { e ->
                Log.e("OrderdetailsActivity", "Lỗi khi lấy dữ liệu đơn hàng: ", e)
                Toast.makeText(this, "Không thể tải đơn hàng.", Toast.LENGTH_SHORT).show()
            }
    }
}
