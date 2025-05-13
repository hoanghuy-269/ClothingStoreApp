package com.example.clothingstoreapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.models.CartItem
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.OrderItemLayoutBinding

class OrderItemAdapter(private val itemList: MutableList<CartItem>,
    private val updateQuantity : (CartItem) ->Unit,
    private val removeQuantity : (CartItem) ->Unit

) : RecyclerView.Adapter<OrderItemAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(private val binding: OrderItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.productName.text = item.name
            binding.productSize.text = "Size: ${item.selectedSize}"
            binding.productPrice.text = "$${item.price * item.quantity}"
            binding.textQuantity.text = "${item.quantity}"
            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .error(R.drawable.img_item_wishlist)
                .into(binding.imageProduct)
            binding.btnIncrease.setOnClickListener{
                item.quantity++
                notifyItemChanged(adapterPosition)
                updateQuantity(item)
            }
            binding.btnDecrease.setOnClickListener{
               if(item.quantity>1){
                   item.quantity--
                   notifyItemChanged(adapterPosition)
                   updateQuantity(item)
               }else{
                   removeQuantity(item)
                   removeItem(adapterPosition)
               }
            }
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

    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }
}