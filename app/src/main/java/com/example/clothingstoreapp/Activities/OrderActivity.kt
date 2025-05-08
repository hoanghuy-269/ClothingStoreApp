package com.example.clothingstoreapp.Activities

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.OrderItemAdapter
import com.example.clothingstoreapp.Model.CartItem
import com.example.clothingstoreapp.databinding.OrderLayoutBinding

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: OrderLayoutBinding
    private lateinit var selectedItems: MutableList<CartItem>

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrderLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedItems = intent.getParcelableArrayListExtra("selectedItems", CartItem::class.java)?.toMutableList() ?: mutableListOf()

        val productId = intent.getStringExtra("productId")
        val productName = intent.getStringExtra("productName")
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val selectedSize = intent.getStringExtra("selectedSize") ?: "Không chọn size"
        val quantity = intent.getIntExtra("quantity", 1)

        // Thêm sản phẩm vào danh sách
        if (productId != null && productName != null && selectedSize != null) {
            val cartItem = CartItem(
                productId = productId,
                name = productName,
                price = productPrice,
                selectedSize = selectedSize,
                quantity = quantity
            )
            selectedItems.add(cartItem)
        }

        // Hiển thị danh sách sản phẩm trong RecyclerView
        displayOrderItems()
    }

    private fun displayOrderItems() {
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(this)

        val adapter = OrderItemAdapter(selectedItems)
        binding.recyclerViewProducts.adapter = adapter
    }
}