package com.example.clothingstoreapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.adapter.OrderStatusAdapter
import com.example.clothingstoreapp.databinding.TrackOrderBinding
import com.example.clothingstoreapp.models.OrderStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class Track_orderActivity : AppCompatActivity() {

    private lateinit var binding: TrackOrderBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrackOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        setupToolbar()

        val productId = intent.getStringExtra("productId") ?: ""

        if (productId.isNotEmpty()) {
            loadOrderDetails(productId)
        } else {
            binding.tvProductName.text = "Không có thông tin sản phẩm"
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Track Order"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadOrderDetails(productId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrEmpty()) {
            binding.tvProductName.text = "Bạn chưa đăng nhập"
            return
        }

        firestore.collection("orders")
            .document(userId)
            .collection("userOrders")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    var foundItem: Map<String, Any>? = null
                    var foundDoc = documents.documents[0]

                    loop@ for (doc in documents) {
                        val items = doc.get("items") as? List<Map<String, Any>> ?: continue
                        for (item in items) {
                            if (item["productId"] == productId) {
                                foundItem = item
                                foundDoc = doc
                                break@loop
                            }
                        }
                    }

                    if (foundItem != null) {
                        displayOrderItem(foundItem, foundDoc)
                    } else {
                        binding.tvProductName.text = "Không tìm thấy sản phẩm trong đơn"
                    }
                } else {
                    binding.tvProductName.text = "Không tìm thấy đơn hàng"
                }
            }
            .addOnFailureListener {
                binding.tvProductName.text = "Lỗi tải dữ liệu"
            }
    }

    private fun displayOrderItem(item: Map<String, Any>, doc: com.google.firebase.firestore.DocumentSnapshot) {
        binding.tvProductName.text = item["name"] as? String ?: ""
        binding.tvProductSizeQty.text = "Size: ${item["selectedSize"] ?: ""} | Qty: ${item["quantity"] ?: 0} pcs"
        binding.tvProductPrice.text = "${item["price"] ?: 0} đ"

        val imageUrl = item["image"] as? String ?: ""
        Glide.with(this).load(imageUrl).into(binding.imgProduct)

        // Lấy orderId làm Tracking ID
        val orderId = doc.id
        binding.tvValueTrackingId.text = orderId

        // Lấy orderDate làm ngày giao dự kiến
        val orderDateLong = (doc.get("orderDate") as? Number)?.toLong() ?: 0L
        val expectedDate = if (orderDateLong > 0) {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            sdf.format(Date(orderDateLong))
        } else "N/A"
        binding.tvValueExpectedDate.text = expectedDate

        // Lấy trạng thái hiện tại
        val currentStatus = doc.getString("status") ?: ""

        // Lấy lịch sử trạng thái từ Firestore, ví dụ key "statusHistory" lưu List<Map>
        val statusesFromFirebase = doc.get("statusHistory") as? List<Map<String, Any>>

        val statusList = if (statusesFromFirebase != null && statusesFromFirebase.isNotEmpty()) {
            statusesFromFirebase.map { statusMap ->
                val title = statusMap["statusTitle"] as? String ?: "Unknown"
                val time = statusMap["statusTime"] as? String ?: ""
                val iconResId = when (val icon = statusMap["statusIconResId"]) {
                    is Long -> icon.toInt()
                    is Int -> icon
                    else -> R.drawable.ic_heart
                }
                val isCurrent = title.equals(currentStatus, ignoreCase = true)
                OrderStatus(title, time, iconResId, isCurrent)
            }
        } else {
            // fallback nếu không có statusHistory thì tạo list đơn giản trạng thái hiện tại
            listOf(OrderStatus(currentStatus, "", R.drawable.ic_heart, true))
        }

        setupStatusRecyclerView(statusList)
    }

    private fun setupStatusRecyclerView(statusList: List<OrderStatus>) {
        val adapter = OrderStatusAdapter(statusList)
        binding.recyclerViewStatus.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewStatus.adapter = adapter
    }
}
