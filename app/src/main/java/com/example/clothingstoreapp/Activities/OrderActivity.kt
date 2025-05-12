package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.OrderItemAdapter
import com.example.clothingstoreapp.Model.Cart
import com.example.clothingstoreapp.Model.CartItem
import com.example.clothingstoreapp.Repository.CartRepository
import com.example.clothingstoreapp.Repository.OrderRepository
import com.example.clothingstoreapp.databinding.OrderLayoutBinding
import com.example.clothingstoreapp.Model.Order
import com.example.clothingstoreapp.Model.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: OrderLayoutBinding
    private lateinit var selectedItems: MutableList<CartItem>

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrderLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener {
            finish()
        }

        selectedItems = intent.getParcelableArrayListExtra("selectedItems", CartItem::class.java)
            ?.toMutableList() ?: mutableListOf()

        val productId = intent.getStringExtra("productId")
        val productName = intent.getStringExtra("productName")
        val productImage = intent.getStringExtra("productImage")
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val selectedSize = intent.getStringExtra("selectedSize") ?: "Không chọn size"
        val quantity = intent.getIntExtra("quantity", 1)

        // Thêm sản phẩm vào danh sách
        if (productId != null && productName != null && selectedSize != null&&productImage!=null) {
            val cartItem = CartItem(
                productId = productId,
                name = productName,
                imageUrl = productImage,
                price = productPrice,
                selectedSize = selectedSize,
                quantity = quantity
            )
            selectedItems.add(cartItem)
        }

        // Hiển thị danh sách sản phẩm trong RecyclerView
        displayOrderItems()
        onProcessToCheckout()
    }

    private fun displayOrderItems() {
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(this)

        val adapter = OrderItemAdapter(selectedItems,
            updateQuantity = {

            },
            removeQuantity = {

            })
        binding.recyclerViewProducts.adapter = adapter
    }
    private fun getSelectedOrderItems(): List<OrderItem> {
        return selectedItems.map { cartItem ->
            OrderItem(
                productId = cartItem.productId,
                name = cartItem.name,
                image = cartItem.imageUrl,
                price = cartItem.price,
                selectedSize = cartItem.selectedSize,
                quantity = cartItem.quantity,
                status = "Trong đơn hàng" // Hoặc trạng thái khác nếu cần
            )
        }
    }
    private fun clearCart(selectedOrderItems: List<OrderItem>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val orderRef = db.collection("carts").document(userId)

            // Lấy giỏ hàng hiện tại
            orderRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val existingCart = snapshot.toObject(Cart::class.java)
                    val items = existingCart?.items?.toMutableList() ?: mutableListOf()

                    // Chuyển đổi OrderItem thành CartItem và xóa
                    selectedOrderItems.forEach { orderItem ->
                        items.removeIf { it.productId == orderItem.productId }
                    }

                    // Cập nhật giỏ hàng mới
                    orderRef.update("items", items)
                        .addOnSuccessListener {
                            // Có thể thêm thông báo thành công nếu cần
                        }
                        .addOnFailureListener { e ->
                            Log.e("CartActivity", "Error updating cart: ${e.message}")
                        }
                }
            }.addOnFailureListener { e ->
                Log.e("CartActivity", "Error getting cart: ${e.message}")
            }
        }
    }
    private fun onProcessToCheckout() {
        binding.btnConfirmOrder.setOnClickListener {
            val orderItems = getSelectedOrderItems()

            val orderId = UUID.randomUUID().toString()
            val totalPrice = orderItems.sumOf { it.price * it.quantity }

            // Tạo đối tượng Order
            val newOrder = Order(
                orderId = orderId,
                userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                items = orderItems,
                totalPrice = totalPrice,
                orderDate = System.currentTimeMillis(),
                status = "Đang chờ",

            )

            // Gọi phương thức addOrder
            OrderRepository.addOrder(newOrder,
                onSuccess = {
                    Toast.makeText(this, "Đơn hàng đã được xác nhận!", Toast.LENGTH_SHORT).show()
                    clearCart(orderItems) // Chỉ xóa các sản phẩm đã chọn
                    startActivity(Intent(this, SucessOrderActivity::class.java))
                },
                onFailure = { exception ->
                    Toast.makeText(this, "Có lỗi xảy ra: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

}