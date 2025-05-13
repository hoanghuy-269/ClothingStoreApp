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
    private var allOrdersList = mutableListOf<OrderItem>()  // Lưu trữ tất cả đơn hàng
    private var filteredOrdersList = mutableListOf<OrderItem>()  // Lưu trữ đơn hàng đã lọc theo trạng thái

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Lấy userId từ Firebase Authentication
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            fetchOrderDetails(it)  // Gọi hàm fetchOrderDetails để lấy tất cả đơn hàng của người dùng
        } ?: run {
            Toast.makeText(this, "Không tìm thấy người dùng.", Toast.LENGTH_SHORT).show()
        }

        // Thiết lập các nút trạng thái
        setupStatusButtons()
    }

    private fun setupStatusButtons() {
        binding.btnAll.setOnClickListener { filterOrdersByStatus("All") }
        binding.btnPending.setOnClickListener { filterOrdersByStatus("Pending") }
        binding.btnShipping.setOnClickListener { filterOrdersByStatus("Shipping") }
        binding.btnCompleted.setOnClickListener { filterOrdersByStatus("Completed") }
        binding.btnCancelled.setOnClickListener { filterOrdersByStatus("Cancelled") }
    }

    // Lọc đơn hàng theo trạng thái được chọn
    private fun filterOrdersByStatus(status: String) {
        filteredOrdersList.clear()
        when (status) {
            "Pending" -> filteredOrdersList.addAll(allOrdersList.filter { it.status == "Pending" })
            "Shipping" -> filteredOrdersList.addAll(allOrdersList.filter { it.status == "Shipping" })
            "Completed" -> filteredOrdersList.addAll(allOrdersList.filter { it.status == "Completed" })
            "Cancelled" -> filteredOrdersList.addAll(allOrdersList.filter { it.status == "Cancelled" })
            else -> filteredOrdersList.addAll(allOrdersList)  // "All"
        }

        updateRecyclerView()  // Cập nhật RecyclerView với dữ liệu đã lọc
    }

    private fun updateRecyclerView() {
        if (filteredOrdersList.isEmpty()) {
            binding.recyclerViewOrders.visibility = View.GONE
            binding.noOrdersMessage.visibility = View.VISIBLE  // Hiển thị thông báo nếu không có đơn hàng
        } else {
            binding.recyclerViewOrders.visibility = View.VISIBLE
            binding.noOrdersMessage.visibility = View.GONE
            val orderAdapter = OrderAdapter(filteredOrdersList)  // Cập nhật RecyclerView với dữ liệu đã lọc
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
                if (documents.isEmpty) {
                    binding.noOrdersMessage.visibility = View.VISIBLE
                } else {
                    documents.forEach { document ->
                        val orderItems = document.get("items") as? List<Map<String, Any>>
                        orderItems?.forEach { item ->
                            // Log item để kiểm tra
                            Log.d("OrderItem", item.toString())

                            val productId = item["productId"] as? String ?: ""
                            val image = item["image"] as? String ?: ""
                            val name = item["name"] as? String ?: ""
                            val price = (item["price"] as? Number)?.toDouble() ?: 0.0
                            val quantity = (item["quantity"] as? Number)?.toInt() ?: 0
                            val selectedSize = item["selectedSize"] as? String ?: "M"
                            val status = item["status"] as? String ?: ""

                            // Tạo OrderItem
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
                            Log.d("OrderAdded", order.toString())  // Log để kiểm tra đã thêm vào danh sách
                        }
                    }
                    updateRecyclerView()  // Cập nhật RecyclerView với dữ liệu đã lấy
                }
            }
            .addOnFailureListener { e ->
                Log.e("OrderdetailsActivity", "Error fetching orders: ", e)
                Toast.makeText(this, "Cannot fetch order data.", Toast.LENGTH_SHORT).show()
            }
    }
}