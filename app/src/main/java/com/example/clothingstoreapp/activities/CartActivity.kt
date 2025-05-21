package com.example.clothingstoreapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.models.Cart
import com.example.clothingstoreapp.models.CartItem
import com.example.clothingstoreapp.adapter.CartItemAdapter
import com.example.clothingstoreapp.databinding.CartLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class CartActivity : Fragment() {

    private var _binding: CartLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartItemAdapter
    private var promoCodeDiscount = 0.0
    private var originalItems: MutableList<CartItem> = mutableListOf()
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CartLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Thiết lập RecyclerView
        binding.recyclerCartItems.layoutManager = LinearLayoutManager(requireContext())
        loadCartData()
        updateTotalCost()
        // Xử lý sự kiện nhấn nút "Apply"
        binding.btnCheckout.setOnClickListener {
            proceedToCheckout()
        }
    }

    private fun updateTotalCost() {
        var subTotal = 0.0
        originalItems.forEach { item ->
            if (item.isSelected) {
                subTotal += item.price * item.quantity
            }
        }

        val totalCost = subTotal - promoCodeDiscount
        val deliveryFee = 25.00
        val totalAmount = totalCost + deliveryFee

        binding.tvSubTotal.text = "Sub-Total: $$subTotal"
        binding.tvDeliveryFee.text = "Delivery Fee: $$deliveryFee"
        binding.tvDiscount.text = "Discount: -$$promoCodeDiscount"
        binding.tvTotalCost.text = "Total Cost: $$totalAmount"
    }

    private fun proceedToCheckout() {
        val selectedItems = originalItems.filter { it.isSelected }

        if (selectedItems.isNotEmpty()) {
            val intent = Intent(requireContext(), OrderActivity::class.java)
            intent.putParcelableArrayListExtra(
                "selectedItems",
                ArrayList(selectedItems)
            )
            startActivity(intent)
        } else {
            showSnackbar("Vui lòng chọn ít nhất một sản phẩm để thanh toán.")
        }
    }

    private fun loadCartData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val orderRef = db.collection("carts").document(userId) // Sử dụng userId làm ID giỏ hàng

            orderRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val existingCart = snapshot.toObject(Cart::class.java)
                    val items = existingCart?.items?.map { item ->
                        CartItem(
                            productId = item.productId,
                            name = item.name,
                            price = item.price,
                            imageUrl = item.imageUrl,
                            selectedSize = item.selectedSize,
                            quantity = item.quantity
                        )
                    }?.toMutableList() ?: mutableListOf()

                    if (items.isNotEmpty()) {
                        originalItems = items
                        cartAdapter = CartItemAdapter(
                            items,
                            { updatedItem -> updateCartInFirestore(updatedItem) },
                            { itemToRemove -> removeItemFromCart(itemToRemove) },
                            { item -> updateTotalCost() } // Cập nhật tổng chi phí khi checkbox thay đổi
                        )
                        binding.recyclerCartItems.adapter = cartAdapter
                    } else {
                        showSnackbar("Giỏ hàng trống")
                    }
                } else {
                    createNewCartInFirestore(userId) // Tạo giỏ hàng mới cho user
                }
            }.addOnFailureListener { e ->
                showSnackbar("Lỗi: ${e.message}")
            }
        } else {
            showSnackbar("Vui lòng đăng nhập để xem giỏ hàng.")
        }
    }

    private fun createNewCartInFirestore(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val orderRef = db.collection("carts").document(userId)

        val newOrder = Cart(
            userId = userId,
            createdDate = System.currentTimeMillis().toString(),
            items = emptyList() // Giỏ hàng ban đầu trống
        )

        orderRef.set(newOrder)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                showSnackbar("Lỗi: ${e.message}")
            }
    }


    private fun removeItemFromCart(item: CartItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val orderRef = db.collection("carts").document(userId)

            orderRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val existingOrder = snapshot.toObject(Cart::class.java)
                    val items = existingOrder?.items?.toMutableList() ?: mutableListOf()

                    val matchedIndex = items.indexOfFirst { it.productId == item.productId }
                    if (matchedIndex != -1) {
                        items.removeAt(matchedIndex)
                    }

                    orderRef.update("items", items)
                        .addOnSuccessListener {
                            showSnackbar("Sản phẩm đã được xóa khỏi giỏ hàng")
                        }
                        .addOnFailureListener { e ->
                            showSnackbar("Lỗi: ${e.message}")
                        }
                }
            }
        }
    }

    private fun updateCartInFirestore(updatedItem: CartItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val orderRef = db.collection("carts").document(userId)
            orderRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val existingOrder = snapshot.toObject(Cart::class.java)
                    val items = existingOrder?.items?.toMutableList() ?: mutableListOf()

                    val matchedIndex = items.indexOfFirst { it.productId == updatedItem.productId }
                    if (matchedIndex != -1) {
                        if (updatedItem.quantity <= 0) {
                            // Nếu số lượng bằng 0, xóa item khỏi giỏ hàng
                            items.removeAt(matchedIndex)
                        } else {
                            // Cập nhật số lượng
                            items[matchedIndex] = updatedItem
                        }
                    }

                    orderRef.update("items", items)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                            showSnackbar("Lỗi: ${e.message}")
                        }
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}