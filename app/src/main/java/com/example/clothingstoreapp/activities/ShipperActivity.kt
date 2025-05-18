package com.example.clothingstoreapp.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.adapter.ShipperAdapter
import com.example.clothingstoreapp.models.Order
import com.example.clothingstoreapp.databinding.ShipperOderLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

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

        val userId = "PD0kQYJ2mIgrYibNa3UJAM4DZ0I3"
        loadTatCaDonHang(userId) // Gọi phương thức với userId

    }

    private fun loadTatCaDonHang(userId: String) {
        orderList.clear() // Xóa danh sách trước khi tải mới

        db.collection("orders")
            .document(userId)
            .collection("userOrders")
            .whereIn("status", listOf("Đang giao", "Chờ xác nhận")) // Lọc theo trạng thái
            .get()
            .addOnSuccessListener { userOrderDocs ->
                if (userOrderDocs.isEmpty) {
                    Toast.makeText(this, "Không có đơn hàng nào để hiển thị", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                for (userOrderDoc in userOrderDocs) {
                    val order = userOrderDoc.toObject<Order>().apply {
                        orderId = userOrderDoc.id
                        this.userId = userId // Gán userId từ tham số
                    }
                    orderList.add(order)
                    Log.d("FirestoreDebug", "Đã thêm đơn hàng: ${order.orderId}, userId: ${order.userId}")
                }

                adapter.notifyDataSetChanged() // Cập nhật RecyclerView
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi tải đơn hàng: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun capNhatTrangThai(order: Order) {
        val userId = order.userId
        val orderId = order.orderId

        // Kiểm tra thông tin đơn hàng
        if (userId.isEmpty() || orderId.isEmpty()) {
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
                        item.status = "Đã giao"
                    }
                    orderData.status = "Đã giao"

                    db.collection("orders")
                        .document(userId)
                        .collection("userOrders")
                        .document(orderId)
                        .set(orderData) // Ghi đè toàn bộ tài liệu để đảm bảo dữ liệu nhất quán
                        .addOnSuccessListener {
                            Toast.makeText(this, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show()
                            loadTatCaDonHang(userId) // Tải lại danh sách để làm mới giao diện
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