package com.example.clothingstoreapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.adapter.ShipperAdapter
import com.example.clothingstoreapp.models.Order
import com.example.clothingstoreapp.databinding.ShipperOderLayoutBinding
import com.example.clothingstoreapp.repositories.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlin.math.log

class ShipperActivity : AppCompatActivity() {
    private lateinit var binding: ShipperOderLayoutBinding
    private lateinit var orderList: MutableList<Order>
    private lateinit var adapter: ShipperAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ShipperOderLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderList = mutableListOf()
        adapter = ShipperAdapter(this, orderList) { order ->
            capNhatTrangThai(order)
        }

        binding.lstShipperOder.adapter = adapter
        binding.lstShipperOder.layoutManager = LinearLayoutManager(this)

        setOnClickButtonCategory()
        loadOrdersByStatus("Shipping")
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("LogOut")
                .setMessage("Nhấn vào CÓ nếu bạn thoát ứng dụng.")
                .setPositiveButton("CÓ") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(this, "Đã thoát ứng dụng thành công", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Không", null)
                .show()
        }
    }

    private fun setOnClickButtonCategory() {
        binding.btncompleted.setOnClickListener {
            loadOrdersByStatus("Completed")
        }
        binding.btnShipping.setOnClickListener {
            loadOrdersByStatus("Shipping")
        }
        binding.btnHistory.setOnClickListener {
            loadTatCaDonHang()
        }
    }

    private fun loadTatCaDonHang() {
        OrderRepository.getAllOrders { orders ->
            updateOrderList(orders)
        }
    }

    private fun loadOrdersByStatus(status: String) {
        OrderRepository.getOrdersByStatus(status) { orders ->
            updateOrderList(orders)
        }
    }

    private fun updateOrderList(orders: List<Order>) {
        if (orders.isEmpty()) {
            binding.emptyMessage.text = "Chưa có đơn hàng"
            binding.emptyMessage.visibility = View.VISIBLE
            binding.lstShipperOder.visibility = View.GONE
        } else {
            binding.emptyMessage.visibility = View.GONE
            binding.lstShipperOder.visibility = View.VISIBLE
            orderList.clear()
            orderList.addAll(orders)
            adapter.notifyDataSetChanged()
        }
    }

    private fun capNhatTrangThai(order: Order) {
        val userId = order.userId
        val orderId = order.orderId

        if (userId.isNullOrEmpty() || orderId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("orders")
            .document(userId)
            .collection("userOrders")
            .document(orderId)
            .get()
            .addOnSuccessListener { document ->
                val orderData = document.toObject<Order>()
                if (orderData != null) {
                    orderData.items?.forEach { item ->
                        item.status = "Completed"
                    }
                    orderData.status = "Completed"

                    db.collection("orders")
                        .document(userId)
                        .collection("userOrders")
                        .document(orderId)
                        .set(orderData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show()
                            loadTatCaDonHang() // Tải lại danh sách mặc định
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Lỗi khi cập nhật trạng thái: $e", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi tải đơn hàng: $e", Toast.LENGTH_SHORT).show()
            }
    }
}
