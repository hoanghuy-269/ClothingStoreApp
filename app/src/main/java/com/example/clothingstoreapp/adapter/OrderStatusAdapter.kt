package com.example.clothingstoreapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.databinding.ItemOrderStatusBinding
import com.example.clothingstoreapp.models.OrderStatus

class OrderStatusAdapter(private val statusList: List<OrderStatus>) :
    RecyclerView.Adapter<OrderStatusAdapter.StatusViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val binding = ItemOrderStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.bind(statusList[position])
    }

    override fun getItemCount(): Int = statusList.size

    class StatusViewHolder(private val binding: ItemOrderStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderStatus: OrderStatus) {
            binding.tvStatusTitle.text = orderStatus.statusTitle
            binding.tvStatusTime.text = orderStatus.statusTime

            Glide.with(binding.imgStatusIcon.context)
                .load(orderStatus.statusIconResId)
                .into(binding.imgStatusIcon)

            // Ví dụ highlight trạng thái hiện tại
            if (orderStatus.isCurrent) {
                binding.tvStatusTitle.setTextColor(binding.root.context.getColor(android.R.color.holo_blue_dark))
            } else {
                binding.tvStatusTitle.setTextColor(binding.root.context.getColor(android.R.color.black))
            }
        }
    }
}
