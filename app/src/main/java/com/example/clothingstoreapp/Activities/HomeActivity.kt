package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.clothingstoreapp.Adapter.BannerAdapter
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Adapter.ProductAdapter
import com.example.clothingstoreapp.Model.Product
import com.example.clothingstoreapp.Repository.WishListRepository
import com.example.clothingstoreapp.databinding.HomeLayoutBinding

import com.example.clothingstoreapp.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : Fragment() {

    private var _binding: HomeLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var bannerViewPager: ViewPager2
    private val bannerHandler = Handler(Looper.getMainLooper())
    private var bannerIndex = 0
    private lateinit var productAdapter: ProductAdapter
    private lateinit var originalList: List<Product>
    private val userFavoriteIds = mutableSetOf<String>()
    private val banners = listOf(
        R.drawable.thonggay,
        R.drawable.tunggay,
        R.drawable.huy
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            checkAndCreateUserDocument(userId)
            loadProducts()
        }

        setupBanner()
    }

    private fun setupBanner() {
        bannerViewPager = binding.bannerViewPager
        bannerViewPager.adapter = BannerAdapter(banners)

        val runnable = object : Runnable {
            override fun run() {
                bannerIndex = (bannerIndex + 1) % banners.size
                bannerViewPager.setCurrentItem(bannerIndex, true)
                bannerHandler.postDelayed(this, 5000)
            }
        }
        bannerHandler.postDelayed(runnable, 3000)
    }
    private fun loadProducts() {
        WishListRepository.getFavoriteProductIds({ favoriteIds ->
            userFavoriteIds.clear()
            userFavoriteIds.addAll(favoriteIds)

            ProductRepository.getAllProducts(
                onSuccess = { productList ->
                    originalList = productList
                    binding.rvProducts.layoutManager = GridLayoutManager(context, 2)
                    productAdapter = ProductAdapter(
                        originalList,
                        userFavoriteIds,
                        onItemClick = { product ->
                            val intent = Intent(requireContext(), ProductdetailsMainActivity::class.java)
                            intent.putExtra("PRODUCT_ID", product.id)
                            startActivity(intent)
                        },
                        onAddFavorite = { productId ->
                            if (!userFavoriteIds.contains(productId)) {
                                WishListRepository.addFavorite(productId, {
                                    Toast.makeText(requireContext(), "Đã thêm yêu thích", Toast.LENGTH_SHORT).show()
                                    userFavoriteIds.add(productId)
                                    productAdapter.notifyDataSetChanged()
                                }, { e ->
                                    Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                                    Log.e("AddFavoriteError", e.message ?: "Không rõ lỗi")
                                })
                            } else {
                                Toast.makeText(requireContext(), "Sản phẩm đã là yêu thích", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onRemoveFavorite = { _ -> } // Không sử dụng
                    )

                    binding.rvProducts.adapter = productAdapter
                    binding.edtSearch.isEnabled = true
                    setupSearchListener()
                },
                onFailure = { e ->
                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }, { e ->
            Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
        })
    }



    private fun setupSearchListener() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                productAdapter.filter(query)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun checkAndCreateUserDocument(userId: String) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                Log.d("Firestore", "Tài liệu người dùng đã tồn tại.")
            } else {
                userRef.set(mapOf("favorites" to emptyList<String>()))
                    .addOnSuccessListener {
                        Log.d("Firestore", "Tài liệu người dùng đã được tạo thành công.")
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
