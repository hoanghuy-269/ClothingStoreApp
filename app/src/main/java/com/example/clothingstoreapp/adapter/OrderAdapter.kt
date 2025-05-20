package com.example.clothingstoreapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.databinding.ItemOrderdetailsaLayoutBinding
import com.example.clothingstoreapp.models.OrderItem

class OrderAdapter(
    private val orders: MutableList<OrderItem>,
    private val onItemClick: (OrderItem) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    fun updateData(newOrders: List<OrderItem>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderdetailsaLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(
        private val binding: ItemOrderdetailsaLayoutBinding,
        private val onItemClick: (OrderItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderItem) {
            binding.txtProductName.text = order.name
            binding.txtProductSize.text = "Size: ${order.selectedSize}"
            binding.txtStatus.text = order.status
            binding.txtProductPrice.text = "${order.price} đ"
            binding.txtQuantity.text = "x${order.quantity}"
            binding.txtTotalPrice.text = "Tổng cộng: ${order.price * order.quantity} đ"

            Glide.with(binding.imgProduct.context)
                .load(order.image)
                .into(binding.imgProduct)

            binding.root.setOnClickListener {
                onItemClick(order)
            }
        }
    }
}
