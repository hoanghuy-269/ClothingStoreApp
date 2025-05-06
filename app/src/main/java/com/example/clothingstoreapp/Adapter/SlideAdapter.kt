package com.example.clothingstoreapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.Model.Slides
import com.example.clothingstoreapp.databinding.ItemSlideLayoutBinding


class SlideAdapter(private val items: List<Slides>) : RecyclerView.Adapter<SlideAdapter.SlideViewHolder>() {

    class SlideViewHolder(val binding: ItemSlideLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val binding = ItemSlideLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

