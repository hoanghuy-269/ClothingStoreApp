package com.example.clothingstoreapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.ItemWishlistLayoutBinding
import com.example.clothingstoreapp.model.Product

class WishListAdapter(
    private val wishList: List<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onRemoveFavorite: (String) -> Unit
) : RecyclerView.Adapter<WishListAdapter.WishListViewHolder>() {

    inner class WishListViewHolder(val binding: ItemWishlistLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.textName.text = product.name
            binding.textPrice.text = "${product.price} đ"
            binding.textRating.text = "${product.rating}"

            Glide.with(binding.root.context)
                .load(product.images.firstOrNull())
                .error(R.drawable.img_item_wishlist)
                .into(binding.imageProduct)

            binding.imageFavorite.setImageResource(R.drawable.ic_heart_red)

            // Xử lý khi click vào bỏ yêu thích
            binding.imageFavorite.setOnClickListener {
                onRemoveFavorite(product.id)
            }

            binding.root.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListViewHolder {
        val binding =
            ItemWishlistLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WishListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WishListViewHolder, position: Int) {
        holder.bind(wishList[position])
    }

    override fun getItemCount(): Int = wishList.size
}