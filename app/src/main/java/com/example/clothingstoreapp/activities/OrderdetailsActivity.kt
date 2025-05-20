package com.example.clothingstoreapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.adapter.OrderAdapter
import com.example.clothingstoreapp.databinding.ActivityOrderdetailsBinding
import com.example.clothingstoreapp.models.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderdetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderdetailsBinding
    private lateinit var firestore: FirebaseFirestore
    private val allOrdersList = mutableListOf<OrderItem>()
    private val filteredOrdersList = mutableListOf<OrderItem>()
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupStatusButtons()
        binding.btnBack.setOnClickListener { finish() }

        firestore = FirebaseFirestore.getInstance()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            fetchOrderDetails(userId)
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng.", Toast.LENGTH_SHORT).show()
            binding.noOrdersMessage.visibility = View.VISIBLE
            binding.recyclerViewOrders.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(mutableListOf()) { clickedOrder ->
            openOrderDetailActivity(clickedOrder)
        }
        binding.recyclerViewOrders.apply {
            layoutManager = LinearLayoutManager(this@OrderdetailsActivity)
            adapter = orderAdapter
        }
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

        val filtered = when (status) {
            "Pending" -> allOrdersList.filter { it.status == "Pending" }
            "Shipping" -> allOrdersList.filter { it.status == "Shipping" }
            "Completed" -> allOrdersList.filter { it.status == "Completed" }
            "Cancelled" -> allOrdersList.filter { it.status == "Cancelled" }
            else -> allOrdersList
        }
        filteredOrdersList.addAll(filtered)

        filteredOrdersList.addAll(
            when (status) {
                "Pending"  -> allOrdersList.filter { it.status == "Pending" }
                "Shipping"  -> allOrdersList.filter { it.status == "Shipping" }
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
            binding.noOrdersMessage.visibility = View.GONE
            binding.recyclerViewOrders.visibility = View.VISIBLE
            orderAdapter.updateData(filteredOrdersList)
        }
    }

    private fun openOrderDetailActivity(order: OrderItem) {
        val intent = Intent(this, Track_orderActivity::class.java).apply {
            putExtra("productId", order.productId)
            putExtra("status", order.status)
        }
        startActivity(intent)
    }

    private fun fetchOrderDetails(userId: String) {
        firestore.collection("orders")
            .document(userId)
            .collection("userOrders")
            .get()
            .addOnSuccessListener { documents ->
                allOrdersList.clear()
                if (documents.isEmpty) {
                    Log.d("OrderdetailsActivity", "Không có đơn hàng cho userId=$userId")
                    binding.noOrdersMessage.visibility = View.VISIBLE
                    binding.recyclerViewOrders.visibility = View.GONE
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
                                    } catch (e: Exception) {
                                        Log.e("ParseError", "Lỗi chuyển đổi đơn hàng: ${e.message}")
                                    }
                                }
                            }
                        } else {
                            Log.e("DataError", "Dữ liệu 'items' không đúng định dạng: $orderItems")
                        }
                    }
                    Log.d("OrderdetailsActivity", "Lấy được tổng đơn hàng: ${allOrdersList.size}")
                    filterOrdersByStatus("All")
                }
            }
            .addOnFailureListener { e ->
                Log.e("OrderdetailsActivity", "Lỗi khi lấy dữ liệu đơn hàng: ", e)
                Toast.makeText(this, "Không thể tải đơn hàng.", Toast.LENGTH_SHORT).show()
                binding.noOrdersMessage.visibility = View.VISIBLE
                binding.recyclerViewOrders.visibility = View.GONE
            }
    }
}
