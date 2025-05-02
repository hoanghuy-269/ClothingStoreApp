package com.example.clothingstoreapp.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.ItemProductBinding
import com.example.clothingstoreapp.model.Product

class ProductAdapter(
    private val originalList: List<Product>,
    private val favoriteIds: MutableSet<String>,
    private val onItemClick: (Product) -> Unit,
    private val onAddFavorite: (String) -> Unit,
    private val onRemoveFavorite: (String) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var filteredList: MutableList<Product> = originalList.toMutableList()

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.txtName.text = product.name
            binding.txtPrice.text = "${product.price} đ"
            binding.txtRating.text = "${product.rating}"

            Glide.with(binding.root.context)
                .load(product.images.firstOrNull())
                .error(R.drawable.img_item_wishlist)
                .into(binding.imgProduct)

            // Xử lý trạng thái yêu thích
            var isFavorite = favoriteIds.contains(product.id)
            binding.imgFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_heart_red else R.drawable.ic_heart
            )

            // Xử lý khi click vào trái tim
            binding.imgFavorite.setOnClickListener {
                isFavorite = !isFavorite
                if (isFavorite) {
                    onAddFavorite(product.id)
                    (favoriteIds as MutableSet).add(product.id)
                } else {
                    onRemoveFavorite(product.id)
                    (favoriteIds as MutableSet).remove(product.id)
                }
                // Cập nhật lại icon
                notifyItemChanged(adapterPosition)
            }

            binding.root.setOnClickListener {
                onItemClick(product)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun updateData(newList: List<Product>) {
        filteredList = newList.toMutableList()
        notifyDataSetChanged()
    }
}


