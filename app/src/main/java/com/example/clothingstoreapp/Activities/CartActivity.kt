package com.example.clothingstoreapp.Activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Model.OrderItem
import com.example.clothingstoreapp.Adapter.CartItemAdapter
import com.example.clothingstoreapp.databinding.CartLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.snackbar.Snackbar

class CartActivity : Fragment() {

    private var _binding: CartLayoutBinding? = null
    private val binding get() = _binding!! // Binding giúp tránh lỗi khi view bị null
    private lateinit var cartAdapter: CartItemAdapter // Adapter cho RecyclerView
    private var promoCodeDiscount = 0.0 // Mã giảm giá
    private var originalItems: MutableList<OrderItem> = mutableListOf() // Giữ lại dữ liệu gốc của giỏ hàng
    private var isSearching = false // Biến để theo dõi trạng thái tìm kiếm

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CartLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Thiết lập RecyclerView để hiển thị giỏ hàng
        binding.recyclerCartItems.layoutManager = LinearLayoutManager(requireContext())

        // Tải dữ liệu giỏ hàng từ Firestore
        loadCartData()

        // Xử lý sự kiện cho nút back
        binding.ivBack.setOnClickListener {
            if (isSearching) {
                // Nếu đang tìm kiếm, reset lại giỏ hàng và dừng tìm kiếm
                isSearching = false
                resetSearch()
            } else {
                // Nếu không tìm kiếm, quay lại trang trước
                activity?.onBackPressed()
            }
        }

        // Lắng nghe sự kiện nhấn nút "Apply" để áp dụng mã giảm giá hoặc tìm kiếm sản phẩm
        binding.btnApply.setOnClickListener {
            val promoCode = binding.edtPromoCode.text.toString()
            if (promoCode.isNotEmpty()) {
                searchItems(promoCode) // Tìm kiếm sản phẩm trong giỏ hàng
            } else {
                showSnackbar("Vui lòng nhập mã tìm kiếm.")
            }
        }
    }

    private fun loadCartData() {
        val orderId = "current_cart" // ID của đơn hàng trong Firestore
        val db = FirebaseFirestore.getInstance()
        val orderRef = db.collection("orders").document(orderId)

        orderRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val existingOrder = snapshot.toObject(com.example.clothingstoreapp.Model.Order::class.java)
                val items = existingOrder?.items?.map { item ->
                    OrderItem(
                        productId = item.productId,
                        quantity = item.quantity,
                        price = item.price
                    )
                }?.toMutableList() ?: mutableListOf()

                // Lưu lại giỏ hàng gốc
                originalItems = items

                // Kiểm tra nếu giỏ hàng có sản phẩm
                if (items.isNotEmpty()) {
                    // Nếu có sản phẩm, gán dữ liệu cho adapter và cập nhật RecyclerView
                    cartAdapter = CartItemAdapter(
                        items,
                        { updatedItem -> updateCartInFirestore(updatedItem) },
                        { itemToRemove -> removeItemFromCart(itemToRemove) }
                    )
                    binding.recyclerCartItems.adapter = cartAdapter

                    // Tính toán tổng tiền sau khi tải giỏ hàng
                    calculateTotalCost(items)
                } else {
                    // Nếu giỏ hàng trống, hiển thị thông báo
                    showSnackbar("Giỏ hàng trống")
                }
            } else {
                // Nếu không có đơn hàng, tạo mới giỏ hàng và thêm vào Firestore
                createNewCartInFirestore()
            }
        }.addOnFailureListener { e ->
            showSnackbar("Lỗi: ${e.message}")
        }
    }

    private fun searchItems(query: String) {
        // Đặt trạng thái tìm kiếm
        isSearching = true

        // Tìm kiếm sản phẩm trong giỏ hàng theo mã sản phẩm hoặc tên
        val filteredItems = originalItems.filter { it.productId.contains(query, ignoreCase = true) }

        if (filteredItems.isNotEmpty()) {
            // Cập nhật RecyclerView với danh sách sản phẩm tìm được
            cartAdapter = CartItemAdapter(
                filteredItems.toMutableList(),
                { updatedItem -> updateCartInFirestore(updatedItem) },
                { itemToRemove -> removeItemFromCart(itemToRemove) }
            )
            binding.recyclerCartItems.adapter = cartAdapter
            // Cập nhật tổng tiền sau khi lọc sản phẩm
            calculateTotalCost(filteredItems.toMutableList())
        } else {
            showSnackbar("Không tìm thấy sản phẩm nào.")
        }
    }

    private fun resetSearch() {
        // Reset lại giỏ hàng về dữ liệu gốc khi không tìm kiếm nữa
        cartAdapter = CartItemAdapter(
            originalItems,
            { updatedItem -> updateCartInFirestore(updatedItem) },
            { itemToRemove -> removeItemFromCart(itemToRemove) }
        )
        binding.recyclerCartItems.adapter = cartAdapter
        // Cập nhật lại tổng tiền
        calculateTotalCost(originalItems)
    }

    private fun updateTotalCost() {
        // Lấy danh sách các sản phẩm trong giỏ hàng
        val items = cartAdapter.getItems()  // Lấy danh sách items từ adapter
        calculateTotalCost(items)
    }

    private fun calculateTotalCost(items: MutableList<OrderItem>) {
        var subTotal = 0.0
        items.forEach { item ->
            subTotal += item.price * item.quantity
        }

        // Tính tổng tiền sau khi áp dụng mã giảm giá
        val totalCost = subTotal - promoCodeDiscount
        val deliveryFee = 25.00 // Giả sử phí giao hàng cố định
        val totalAmount = totalCost + deliveryFee

        // Cập nhật giao diện với các giá trị tính toán
        binding.tvSubTotal.text = "Sub-Total: $$subTotal"
        binding.tvDeliveryFee.text = "Delivery Fee: $$deliveryFee"
        binding.tvDiscount.text = "Discount: -$$promoCodeDiscount"
        binding.tvTotalCost.text = "Total Cost: $$totalAmount"
    }

    private fun removeItemFromCart(item: OrderItem) {
        val db = FirebaseFirestore.getInstance()
        val orderRef = db.collection("orders").document("current_cart")

        orderRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val existingOrder = snapshot.toObject(com.example.clothingstoreapp.Model.Order::class.java)
                val items = existingOrder?.items?.toMutableList() ?: mutableListOf()

                // Tìm và xóa sản phẩm khỏi giỏ hàng
                val matchedIndex = items.indexOfFirst { it.productId == item.productId }
                if (matchedIndex != -1) {
                    items.removeAt(matchedIndex) // Xóa sản phẩm khỏi giỏ hàng
                }

                // Cập nhật giỏ hàng trong Firestore
                orderRef.update("items", items)
                    .addOnSuccessListener {
                        showSnackbar("Sản phẩm đã được xóa khỏi giỏ hàng")
                        calculateTotalCost(items) // Cập nhật lại tổng tiền sau khi xóa sản phẩm
                    }
                    .addOnFailureListener { e ->
                        showSnackbar("Lỗi: ${e.message}")
                    }
            }
        }
    }

    private fun updateCartInFirestore(updatedItem: OrderItem) {
        val db = FirebaseFirestore.getInstance()
        val orderRef = db.collection("orders").document("current_cart")

        orderRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val existingOrder = snapshot.toObject(com.example.clothingstoreapp.Model.Order::class.java)
                val items = existingOrder?.items?.toMutableList() ?: mutableListOf()

                // Cập nhật thông tin sản phẩm trong giỏ hàng
                val matchedIndex = items.indexOfFirst { it.productId == updatedItem.productId }

                if (matchedIndex != -1) {
                    items[matchedIndex] = updatedItem // Cập nhật sản phẩm với số lượng mới
                }

                // Cập nhật giỏ hàng trong Firestore
                orderRef.update("items", items)
                    .addOnSuccessListener {
                        showSnackbar("Giỏ hàng đã được cập nhật")
                        calculateTotalCost(items) // Cập nhật lại tổng tiền sau khi cập nhật giỏ hàng
                    }
                    .addOnFailureListener { e ->
                        showSnackbar("Lỗi: ${e.message}")
                    }
            }
        }
    }

    private fun createNewCartInFirestore() {
        val db = FirebaseFirestore.getInstance()
        val orderRef = db.collection("orders").document("current_cart")

        val newOrder = com.example.clothingstoreapp.Model.Order(
            userId = "user_01", // Hoặc lấy từ FirebaseAuth nếu có
            orderDate = System.currentTimeMillis().toString(),
            status = "pending",
            totalAmount = 0.0,
            items = mutableListOf()  // Giỏ hàng ban đầu trống
        )

        orderRef.set(newOrder)
            .addOnSuccessListener {
                showSnackbar("Giỏ hàng mới đã được tạo")
            }
            .addOnFailureListener { e ->
                showSnackbar("Lỗi: ${e.message}")
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
