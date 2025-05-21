package com.example.clothingstoreapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.adapter.OrderItemAdapter
import com.example.clothingstoreapp.models.Cart
import com.example.clothingstoreapp.models.CartItem
import com.example.clothingstoreapp.repositories.OrderRepository
import com.example.clothingstoreapp.databinding.OrderLayoutBinding
import com.example.clothingstoreapp.models.Order
import com.example.clothingstoreapp.models.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: OrderLayoutBinding
    private lateinit var selectedItems: MutableList<CartItem>
    private val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrderLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadProfile()
        binding.btnBack.setOnClickListener {
            finish()
        }
        setContent()

        selectedItems = intent.getParcelableArrayListExtra("selectedItems", CartItem::class.java)
            ?.toMutableList() ?: mutableListOf()

        val productId = intent.getStringExtra("productId")
        val productName = intent.getStringExtra("productName")
        val productImage = intent.getStringExtra("productImage")
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val selectedSize = intent.getStringExtra("selectedSize") ?: "Không chọn size"
        val quantity = intent.getIntExtra("quantity", 1)

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

        displayOrderItems()
        onProcessToCheckout()
        loadAddressFromFirebase()
    }
    private fun setContent()
    {
        binding.linearlayoutProfile.setOnClickListener {
            openAddressScreen()
        }
    }

    private fun updateTotalAmount() {
        val totalPrice = selectedItems.sumOf { it.price * it.quantity }
        binding.totalAmount.text = "Tổng tiền: ${totalPrice} VNĐ"
    }

    private fun displayOrderItems() {
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(this)

        val adapter = OrderItemAdapter(selectedItems,
            updateQuantity = {
                updateTotalAmount()
            },
            removeQuantity = {
                updateTotalAmount()
            })
        binding.recyclerViewProducts.adapter = adapter
        updateTotalAmount()
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
                status = "Pending"
            )
        }
    }

    private fun clearCart(selectedOrderItems: List<OrderItem>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val orderRef = db.collection("carts").document(userId)

            orderRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val existingCart = snapshot.toObject(Cart::class.java)
                    val items = existingCart?.items?.toMutableList() ?: mutableListOf()

                    selectedOrderItems.forEach { orderItem ->
                        items.removeIf { it.productId == orderItem.productId }
                    }

                    orderRef.update("items", items)
                        .addOnSuccessListener {
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
            val address = binding.txtAddress.text.toString()
            // Tạo đối tượng Order
            val newOrder = Order(
                orderId = orderId,
                userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                items = orderItems,
                totalPrice = totalPrice,
                orderDate = System.currentTimeMillis(),
                status = "Đang chờ",
                address = address
            )

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


    private fun loadProfile()
    {
        val  uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null)
        {
            Toast.makeText(this," Đăng nhập người dùng để hiển thị",Toast.LENGTH_SHORT).show()
            return
        }
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val phone = document.getString("phone") ?: ""
                val name = document.getString("name") ?: ""
                binding.txtName.setText(name)
                binding.txtSDT.setText(phone)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Không thể tải dữ liệu: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private val addressLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result-> if (result.resultCode ==Activity.RESULT_OK){
        val fullAddress = result.data?.getStringExtra("selectedAddress")
        val detail = result.data?.getStringExtra("detailAddress")

        val hienthi = if(detail.isNullOrEmpty()){
            "$detail,$fullAddress"
        }
        else
        {
            fullAddress
        }
        binding.txtAddress.text = hienthi

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if(uid != null && fullAddress != null)
        {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid)
                .update("address",fullAddress)
                .addOnSuccessListener {
                    Toast.makeText(this," cập nhật địa chỉ thành công",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Log.e("update","Lỗi khi cập nhât ${it.message}")
                }
        }
    }
    }
    private fun openAddressScreen() {
        val intent = Intent(this, AddressActivity::class.java)
        addressLauncher.launch(intent)
    }
    private fun loadAddressFromFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val userRef = FirebaseFirestore.getInstance().collection("users").document(uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val address = document.getString("address")
                        binding.txtAddress.text = address ?: "Chưa có địa chỉ"
                    } else {
                        binding.txtAddress.text = "Chưa có địa chỉ"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Lỗi khi tải địa chỉ: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
        }
    }
}