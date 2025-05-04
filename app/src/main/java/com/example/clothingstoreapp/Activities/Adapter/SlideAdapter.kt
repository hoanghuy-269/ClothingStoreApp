package com.example.clothingstoreapp.Activities.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.Activities.model.slideItem
import com.example.clothingstoreapp.databinding.ItemSlideBinding

class SlideAdapter(private val items: List<slideItem>) : RecyclerView.Adapter<SlideAdapter.SlideViewHolder>() {

    class SlideViewHolder(val binding: ItemSlideBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val binding = ItemSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SlideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        val slideItem = items[position]
        holder.binding.imgSlide.setImageResource(slideItem.image)
        holder.binding.tvTitle.text = slideItem.tile
        holder.binding.tvDescription.text = slideItem.description
    }

    override fun getItemCount(): Int = items.size
}
