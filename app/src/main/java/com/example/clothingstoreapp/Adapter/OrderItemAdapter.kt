package com.example.clothingstoreapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.Model.CartItem
import com.example.clothingstoreapp.databinding.CartItemLayoutBinding
import com.example.clothingstoreapp.databinding.OrderItemLayoutBinding

class OrderItemAdapter(private val itemList: List<CartItem>) : RecyclerView.Adapter<OrderItemAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(private val binding: OrderItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.productName.text = item.name
            binding.productPrice.text = "${item.price * item.quantity} đ"
            binding.textQuantity.text = "${item.quantity}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = OrderItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = itemList.size
}