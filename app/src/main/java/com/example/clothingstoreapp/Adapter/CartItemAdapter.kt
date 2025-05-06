package com.example.clothingstoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.model.OrderItem

class CartItemAdapter(
    private val itemList: MutableList<OrderItem>
) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val quantityText: TextView = itemView.findViewById(R.id.textQuantity)
        val btnIncrease: ImageButton = itemView.findViewById(R.id.btnIncrease)
        val btnDecrease: ImageButton = itemView.findViewById(R.id.btnDecrease)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_layout, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = itemList[position]
        holder.quantityText.text = item.quantity.toString()
        holder.productName.text = item.productId
        holder.productPrice.text = "$${item.price * item.quantity}"

        holder.btnIncrease.setOnClickListener {
            item.quantity++
            notifyItemChanged(position)
        }

        holder.btnDecrease.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = itemList.size
}
