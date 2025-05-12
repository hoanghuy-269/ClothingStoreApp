package com.example.clothingstoreapp.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.Model.OrderItem
import com.example.clothingstoreapp.databinding.ItemOrderdetailsaLayoutBinding

class OrderAdapter(private val orders: List<OrderItem>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderdetailsaLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(private val binding: ItemOrderdetailsaLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderItem) {
            binding.txtProductName.text = order.name
            binding.txtProductSize.text = "Size: ${order.selectedSize}"
            binding.txtStatus.text = order.status
            binding.txtProductPrice.text = "${order.price} đ"
            binding.txtQuantity.text = "x${order.quantity}"  // Hiển thị số lượng
            binding.txtTotalPrice.text = "Tổng cộng: ${order.price * order.quantity} đ"  // Tính tổng tiền

            Log.d("OrderViewHolder", "Binding order: $order")  // Log để kiểm tra dữ liệu

            Glide.with(binding.imgProduct.context)
                .load(order.image)
                .into(binding.imgProduct)
        }
    }
}