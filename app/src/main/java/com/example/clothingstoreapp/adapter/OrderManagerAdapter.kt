package com.example.clothingstoreapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.ItemOrderManagerLayoutBinding
import com.example.clothingstoreapp.models.Order
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderManagerAdapter(
    private val onEditClick: (Order) -> Unit,
    private val onDeleteClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderManagerAdapter.OrderManagerViewHolder>() {

    private var orders: List<Order> = emptyList()
    private var expandedPosition = -1

    inner class OrderManagerViewHolder(private val binding: ItemOrderManagerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var currentOrder: Order

        fun bind(order: Order, isExpanded: Boolean) {
            Log.d("OrderManagerAdapter", "Binding order: ${order.orderId}")
            currentOrder = order

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

            // Hiển thị đầy đủ các trường của Order
            binding.textOrderId.text = "Đơn: #${order.orderId}"
            binding.textOrderDate.text = sdf.format(Date(order.orderDate))
            binding.textStatus.text = "Trạng thái: ${order.status ?: "Không xác định"}"
            binding.textTotal.text =
                "Tổng tiền: ${currencyFormat.format(order.totalPrice)} (${order.items.size} sản phẩm)"

            // Các thông tin chi tiết (ẩn mặc định, hiển thị khi mở rộng)
            binding.textAddress.text = "Địa chỉ: ${order.address ?: "Không có địa chỉ"}"
            binding.textDeliveryDate.text =
                "Ngày giao dự kiến: ${sdf.format(Date(order.deliveryDate))}"

            // Chỉ lấy productId từ OrderItem
            val productCodes = if (order.items.isNotEmpty()) {
                order.items.joinToString(", ") { it.productId ?: "N/A" }
            } else {
                "Không có sản phẩm"
            }
            binding.textProductCodes.text = "Mã sản phẩm: $productCodes"

            // Mở rộng / thu gọn chi tiết
            binding.expandedView.visibility = if (isExpanded) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(expandedPosition)
                    expandedPosition = if (isExpanded) -1 else position
                    notifyItemChanged(position)
                }
            }

            setupOptionsMenu()
        }

        private fun setupOptionsMenu() {
            binding.buttonOptions.setOnClickListener { view ->
                PopupMenu(view.context, view).apply {
                    menuInflater.inflate(R.menu.menu_product_item, menu)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.action_edit -> {
                                onEditClick(currentOrder)
                                true
                            }
                            R.id.action_delete -> {
                                onDeleteClick(currentOrder)
                                true
                            }
                            else -> false
                        }
                    }
                    show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderManagerViewHolder {
        Log.d("OrderManagerAdapter", "Creating ViewHolder")
        val binding = ItemOrderManagerLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderManagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderManagerViewHolder, position: Int) {
        if (position < orders.size) { // Kiểm tra vị trí hợp lệ
            val order = orders[position]
            holder.bind(order, position == expandedPosition)
        } else {
            Log.w("OrderManagerAdapter", "Invalid position $position, orders size: ${orders.size}")
        }
    }

    override fun getItemCount(): Int {
        Log.d("OrderManagerAdapter", "Item count: ${orders.size}")
        return orders.size
    }

    fun submitList(newList: List<Order>) {
        Log.d("OrderManagerAdapter", "Submitting list with ${newList.size} orders")
        val diffCallback = OrderDiffCallback(orders, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        orders = newList
        diffResult.dispatchUpdatesTo(this)
    }

    private class OrderDiffCallback(
        private val oldList: List<Order>,
        private val newList: List<Order>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].orderId == newList[newItemPosition].orderId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}