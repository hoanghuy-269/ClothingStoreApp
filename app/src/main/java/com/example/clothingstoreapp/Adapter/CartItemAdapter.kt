package com.example.clothingstoreapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.Model.OrderItem
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.CartItemLayoutBinding

class CartItemAdapter(
    private val itemList: MutableList<OrderItem>
) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: CartItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderItem) {
            binding.productName.text = item.productId
            binding.productPrice.text = "$${item.price * item.quantity}"
            binding.textQuantity.text = item.quantity.toString()

            binding.btnIncrease.setOnClickListener {
                item.quantity++
                notifyItemChanged(adapterPosition)
            }

            binding.btnDecrease.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity--
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size
}