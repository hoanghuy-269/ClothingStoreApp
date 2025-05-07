package com.example.clothingstoreapp.Activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Model.OrderItem
import com.example.clothingstoreapp.Repository.OrderRepository
import com.example.clothingstoreapp.Adapter.CartItemAdapter
import com.example.clothingstoreapp.databinding.CartLayoutBinding

class CartActivity : Fragment() {

    private var _binding: CartLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartItemAdapter

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
    }

    private fun loadCartData() {
        val orderId = "current_cart" // ID của đơn hàng bạn muốn lấy
        OrderRepository.getOrder(orderId) { order ->
            order?.let {
                val items = it.items.map { item ->
                    OrderItem(
                        productId = item.productId,
                        quantity = item.quantity,
                        price = item.price
                    )
                }.toMutableList()

                if (items.isNotEmpty()) {
                    cartAdapter = CartItemAdapter(items)
                    binding.recyclerCartItems.adapter = cartAdapter
                } else {
                    Toast.makeText(requireContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Giải phóng binding để tránh rò rỉ bộ nhớ
    }
}