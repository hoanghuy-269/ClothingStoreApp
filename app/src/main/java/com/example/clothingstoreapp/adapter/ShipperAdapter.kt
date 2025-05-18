package com.example.clothingstoreapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.databinding.ItemShipperOderLayoutBinding
import com.example.clothingstoreapp.models.Order

class ShipperAdapter(
    private val context: Context,
    private val orderList: List<Order>,
    private val giaHang: (Order) -> Unit
) : RecyclerView.Adapter<ShipperAdapter.ShipperViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipperViewHolder {
        val binding = ItemShipperOderLayoutBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ShipperViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShipperViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orderList.size

    inner class ShipperViewHolder(private val binding: ItemShipperOderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.txtMaDon.text = "Mã đơn: ${order.orderId}"
            binding.txtAddress.text = "Địa chỉ: ${order.address}"
            binding.txttotalPrice.text = "Tổng thanh toán: ${order.totalPrice}đ"
            binding.txtTrangThai.text = "Trạng thái: ${order.status}"

            // Nút "Hoàn thành" chỉ bật khi chưa giao
            binding.btnHoanThanh.isEnabled = order.status != "Đã giao"
            binding.btnHoanThanh.setOnClickListener {
                giaHang(order)
            }
        }
    }
}
