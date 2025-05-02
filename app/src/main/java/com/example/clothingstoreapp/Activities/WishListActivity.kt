package com.example.clothingstoreapp.Activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.WishListAdapter
import com.example.clothingstoreapp.databinding.MyWishlistLayoutBinding
import com.example.clothingstoreapp.repository.ProductRepository
import com.example.clothingstoreapp.repository.WishListRepository
import com.google.firebase.auth.FirebaseAuth

class WishListActivity : Fragment() {

    private var _binding: MyWishlistLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var wishListAdapter: WishListAdapter
    private val favoriteIds = mutableSetOf<String>()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyWishlistLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        if (userId != null) {
            loadFavoriteProducts()
        } else {
            Toast.makeText(context, "Vui lòng đăng nhập để xem danh sách yêu thích", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadFavoriteProducts() {
        if (userId != null) {
            WishListRepository.getFavoriteProductIds(userId, { ids ->
                favoriteIds.clear() // Xóa danh sách cũ
                favoriteIds.addAll(ids)
                loadProductDetails(favoriteIds)
            }, { e ->
                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun loadProductDetails(ids: Set<String>) {
        ProductRepository.getProductsByIds(ids, { productList ->
            wishListAdapter = WishListAdapter(productList,
                onRemoveFavorite = { productId ->
                onRemoveFavorite(productId)
            }, onItemClick = { product ->
                // Xử lý sự kiện khi nhấn vào sản phẩm
                Toast.makeText(context, "Đã chọn: ${product.name}", Toast.LENGTH_SHORT).show()
            })

            binding.recyclerView.adapter = wishListAdapter
        }, { e ->
            Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
        })
    }

    private fun onRemoveFavorite(productId: String) {
        WishListRepository.removeFavorite(userId!!, productId, {
            favoriteIds.remove(productId)
            loadFavoriteProducts()
        }, { e ->
            Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Giải phóng binding để tránh rò rỉ bộ nhớ
    }
}