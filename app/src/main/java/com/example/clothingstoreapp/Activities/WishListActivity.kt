package com.example.clothingstoreapp.Activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
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
        setOnClickButtonCategory() // Gọi tại đây
        binding.recyclerView.layoutManager = LinearLayoutManager(context)


        if (userId != null) {
            loadFavoriteProducts()
        } else {
            showToast("Vui lòng đăng nhập để xem danh sách yêu thích")
        }
    }

    private fun loadFavoriteProducts() {
        if (userId != null) {
            WishListRepository.getFavoriteProductIds(userId, { ids ->
                favoriteIds.clear()
                favoriteIds.addAll(ids)
                loadProductDetails(favoriteIds)
            }, { e ->
                showToast("Lỗi: ${e.message}")
            })
        }
    }

    private fun loadProductDetails(ids: Set<String>) {
        ProductRepository.getProductsByIds(ids, { productList ->
            wishListAdapter = WishListAdapter(productList,
                onRemoveFavorite = { productId -> onRemoveFavorite(productId) },
                onItemClick = { product ->
                    showToast("Đã chọn: ${product.name}")
                }

            )
            binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
            binding.recyclerView.adapter = wishListAdapter
            binding.edtSearch.isEnabled = true
            searchFavoriteProduct()
        }, { e ->
            showToast("Lỗi: ${e.message}")
        })
    }

    private fun onRemoveFavorite(productId: String) {
        WishListRepository.removeFavorite(userId!!, productId, {
            favoriteIds.remove(productId)
            loadFavoriteProducts()
        }, { e ->
            showToast("Lỗi: ${e.message}")
        })
    }
    private fun searchFavoriteProduct(){
        binding.edtSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                wishListAdapter.filter(query)
            }
        })



    }
    private fun setOnClickButtonCategory() {
        binding.btnAll.setOnClickListener { loadFavoriteProducts() }
        binding.btnShirt.setOnClickListener { loadProductByCategory("1") }
        binding.btnJacket.setOnClickListener { loadProductByCategory("2") }
        binding.btnPant.setOnClickListener { loadProductByCategory("3") }
        binding.btnTShirt.setOnClickListener { loadProductByCategory("4") }
    }

    private fun loadProductByCategory(category: String?) {
        if (category != null) {
            WishListRepository.getFavoriteProductsByCategory(userId!!, category, onSuccess = { productList ->
                loadProductDetails(productList.map { it.id }.toSet())
            }, onFailure = { exception ->
                showToast("Lỗi: ${exception.message}")
            })
        } else {
            showToast("Danh mục không hợp lệ")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}