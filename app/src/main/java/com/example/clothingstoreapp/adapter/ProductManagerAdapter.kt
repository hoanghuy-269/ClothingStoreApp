package com.example.clothingstoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.ItemProductManagerBinding
import com.example.clothingstoreapp.models.Product

class ProductManagerAdapter(
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductManagerAdapter.ProductViewHolder>() {

    private var originalList: List<Product> = emptyList()
    private var products: MutableList<Product> = mutableListOf()
    private var expandedPosition = -1

    inner class ProductViewHolder(private val binding: ItemProductManagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var currentProduct: Product

        fun bind(product: Product, isExpanded: Boolean) {
            currentProduct = product

            Glide.with(binding.root.context)
                .load(product.images)
                .placeholder(R.drawable.img_item_wishlist)
                .into(binding.imageProduct)

            binding.textProductName.text = product.name
            binding.textProductPrice.text = "Giá: ${product.price}đ"
            binding.textProductStock.text = "SL: ${product.stock}"
            binding.textProductDescription.text = product.description
            binding.textProductSizes.text = "Kích thước: ${product.sizes.joinToString()}"
            binding.ratingProduct.text = "${product.rating}"
            binding.tvCategory.text = "Category: ${product.categoryId}"
            binding.expandedView.visibility = if (isExpanded) View.VISIBLE else View.GONE

            binding.root.setBackgroundResource(
                if (isExpanded) R.color.soft_beige else android.R.color.transparent
            )
            setupClickListeners(isExpanded)
            setupOptionsMenu()
        }

        private fun setupClickListeners(isExpanded: Boolean) {
            binding.compactView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(expandedPosition)
                    if (isExpanded) {
                        expandedPosition = -1
                    } else {
                        expandedPosition = position
                    }
                    notifyItemChanged(position)
                }
            }
        }

        private fun setupOptionsMenu() {
            binding.buttonOptions.setOnClickListener { view ->
                PopupMenu(view.context, view).apply {
                    menuInflater.inflate(R.menu.menu_product_item, menu)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.action_edit -> {
                                onEditClick(currentProduct)
                                true
                            }
                            R.id.action_delete -> {
                                onDeleteClick(currentProduct)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductManagerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position], position == expandedPosition)
    }

    override fun getItemCount() = products.size

    fun submitList(newList: List<Product>) {
        originalList = newList
        products = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        products.clear()
        if (query.isEmpty()) {
            products.addAll(originalList)
        } else {
            val filteredList = originalList.filter {
                it.name.contains(query, ignoreCase = true)
            }
            products.addAll(filteredList)
        }
        notifyDataSetChanged()
    }


}
