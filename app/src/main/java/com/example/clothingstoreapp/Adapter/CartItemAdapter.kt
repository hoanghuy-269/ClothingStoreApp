package com.example.clothingstoreapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.Model.OrderItem
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.CartItemLayoutBinding

class CartItemAdapter(
    private var itemList: MutableList<OrderItem>,
    private val updateCartCallback: (OrderItem) -> Unit, // Callback để cập nhật Firestore
    private val removeItemCallback: (OrderItem) -> Unit // Callback để xóa sản phẩm khỏi giỏ hàng
) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: CartItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderItem) {
            binding.productName.text = item.productId
            binding.productPrice.text = "$${item.price * item.quantity}"
            binding.textQuantity.text = item.quantity.toString()

            // Lắng nghe sự kiện tăng số lượng
            binding.btnIncrease.setOnClickListener {
                item.quantity++ // Tăng số lượng sản phẩm
                notifyItemChanged(adapterPosition) // Cập nhật lại giao diện
                updateCartCallback(item) // Gọi callback để cập nhật Firestore
            }

            // Lắng nghe sự kiện giảm số lượng
            binding.btnDecrease.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity-- // Giảm số lượng sản phẩm
                    notifyItemChanged(adapterPosition) // Cập nhật lại giao diện
                    updateCartCallback(item) // Gọi callback để cập nhật Firestore
                } else if (item.quantity == 1) {
                    // Nếu số lượng sản phẩm là 1, khi nhấn "-" thì xóa sản phẩm khỏi giỏ hàng
                    removeItemCallback(item) // Gọi callback để xóa sản phẩm khỏi giỏ hàng
                    itemList.remove(item) // Xóa sản phẩm khỏi danh sách trong RecyclerView
                    notifyDataSetChanged() // Cập nhật lại RecyclerView sau khi xóa
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
        holder.bind(itemList[position])
    }

    // Trả về số lượng items trong RecyclerView
    override fun getItemCount(): Int = itemList.size

    // Phương thức getItems() để lấy danh sách item từ adapter
    fun getItems(): MutableList<OrderItem> {
        return itemList
    }
}
