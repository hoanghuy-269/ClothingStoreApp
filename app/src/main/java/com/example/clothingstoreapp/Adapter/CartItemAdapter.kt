package com.example.clothingstoreapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.Model.CartItem
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.CartItemLayoutBinding

class CartItemAdapter(
    private var itemList: MutableList<CartItem>,
    private val updateCartCallback: (CartItem) -> Unit,
    private val removeItemCallback: (CartItem) -> Unit,
    private val onCheckboxChanged: (CartItem) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: CartItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.productName.text = item.name
            binding.productPrice.text = "$${item.price * item.quantity}"
            binding.textQuantity.text = item.quantity.toString()

            binding.cbSelect.setOnCheckedChangeListener(null) // Để tránh kích hoạt listener không mong muốn
            binding.cbSelect.isChecked = item.isSelected // Gán trạng thái checkbox

            binding.cbSelect.setOnCheckedChangeListener { _, isChecked ->
                item.isSelected = isChecked // Cập nhật trạng thái khi checkbox thay đổi
                onCheckboxChanged(item) // Gọi callback để thông báo cho Fragment
            }
            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .error(R.drawable.img_item_wishlist)
                .into(binding.imageProduct)
            // Lắng nghe sự kiện tăng số lượng
            binding.btnIncrease.setOnClickListener {
                item.quantity++
                notifyItemChanged(adapterPosition)
                updateCartCallback(item)
            }

            // Lắng nghe sự kiện giảm số lượng
            binding.btnDecrease.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity--
                    notifyItemChanged(adapterPosition)
                    updateCartCallback(item)
                } else {
                    removeItemCallback(item)
                    removeItem(adapterPosition)
                }
            }
        }
    }

    // Tạo view holder từ layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    // Gán dữ liệu cho từng item trong RecyclerView
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    // Trả về số lượng items trong RecyclerView
    override fun getItemCount(): Int = itemList.size

    // Phương thức để xóa item
    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    // Phương thức để cập nhật danh sách items
    fun updateItems(newItems: MutableList<CartItem>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }

    // Phương thức getItems() để lấy danh sách item từ adapter
    fun getItems(): MutableList<CartItem> {
        return itemList
    }
}