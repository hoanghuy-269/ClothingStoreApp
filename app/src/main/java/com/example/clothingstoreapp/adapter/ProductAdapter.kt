package com.example.clothingstoreapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.ItemProductBinding
import com.example.clothingstoreapp.models.Product


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
            Log.d("ProductAdapter", "Image URL: ${product.images}")
            Glide.with(binding.root.context)
                .load(product.images)
                .error(R.drawable.img_item_wishlist)
                .into(binding.imgProduct)

            val isFavorite = favoriteIds.contains(product.id)
            binding.imgFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_heart_red else R.drawable.ic_heart
            )

            binding.imgFavorite.setOnClickListener {
                val newFavoriteStatus = !isFavorite
                if (newFavoriteStatus) {
                    onAddFavorite(product.id)
                    favoriteIds.add(product.id)
                } else {
                    onRemoveFavorite(product.id)
                    favoriteIds.remove(product.id)
                }
                notifyItemChanged(bindingAdapterPosition)
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
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(originalList)
        } else {
            val filtered = originalList.filter {
                it.name.contains(query, ignoreCase = true)
            }
            filteredList.addAll(filtered)
        }
        notifyDataSetChanged()
    }

    fun updateProductList(newList: List<Product>) {
        filteredList.clear()
        filteredList.addAll(newList)
        notifyDataSetChanged()
    }

}


