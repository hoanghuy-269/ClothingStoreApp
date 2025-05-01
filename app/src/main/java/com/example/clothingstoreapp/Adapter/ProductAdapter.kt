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
    private val productList: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            // Gán dữ liệu vào các view bằng binding
            binding.txtName.text = product.name
            binding.txtPrice.text = "${product.price} đ"
            binding.txtRating.text = "${product.rating}"

            Glide.with(binding.root.context)
                .load(product.images?.trim())
                .error(R.drawable.img_item_wishlist)
                .into(binding.imgProduct)
            Log.d("ImageURL", product.images.toString())

            binding.imgFavorite.setImageResource(R.drawable.ic_heart) // Hoặc ic_heart_filled nếu được yêu thích

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
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size
}
