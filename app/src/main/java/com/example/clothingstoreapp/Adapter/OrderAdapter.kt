package com.example.clothingstoreapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.Model.Orderdetails
import com.example.clothingstoreapp.databinding.ItemOrderdetailsaLayoutBinding

class OrderAdapter(private val orders: List<Orderdetails>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

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
        fun bind(order: Orderdetails) {
            // Liên kết dữ liệu với các view
            binding.txtProductName.text = order.productName
            binding.txtProductSize.text = "Size: ${order.size}"
            binding.txtReturnPolicy.text = order.returnPolicy
            binding.txtProductPrice.text = "${order.price} đ"
            binding.txtQuantity.text = "x${order.quantity}"
            binding.txtTotalPrice.text = "Tổng cộng: ${order.totalPrice} đ"  // Hiển thị totalPrice đã chuyển thành Double

            // Sử dụng Glide để tải ảnh sản phẩm từ URL (hoặc Firebase Storage)
            Glide.with(binding.imgProduct.context)
                .load(order.productImage)
                .into(binding.imgProduct)
        }
    }

}
