package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
import com.example.clothingstoreapp.databinding.HomeLayoutBinding
import com.example.clothingstoreapp.model.Product
import com.example.clothingstoreapp.repository.ProductRepository
import com.google.firebase.Timestamp

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
        R.drawable.huy,
        R.drawable.thonggay,
        R.drawable.tunggay,

        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Khởi tạo ViewBinding
        _binding = HomeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // banner
        bannerViewPager = view.findViewById(R.id.bannerViewPager)
        bannerViewPager.adapter = BannerAdapter(banners)

        val runnable = object : Runnable {
            override fun run() {
                bannerIndex = (bannerIndex + 1) % banners.size
                bannerViewPager.setCurrentItem(bannerIndex, true)
                bannerHandler.postDelayed(this, 5000)
            }
        }
        bannerHandler.postDelayed(runnable, 3000)

        // Ẩn search & kết quả trước khi có dữ liệu
        binding.edtSearch.isEnabled = false
        binding.txtNoResult.visibility = View.GONE

        // Load dữ liệu sản phẩm
        ProductRepository.getAllProducts(
            onSuccess = { productList ->
                originalList = productList
                binding.rvProducts.layoutManager = GridLayoutManager(context, 2)
                productAdapter = ProductAdapter(
                    originalList,
                    userFavoriteIds,
                    onItemClick = { product ->
                        // Xử lý khi click vào sản phẩm
                        val intent = Intent(requireContext(), SignInActivity::class.java)
                        startActivity(intent)
                    },
                    onAddFavorite = { productId ->
                        // Ví dụ: thêm sản phẩm vào danh sách yêu thích
                        Toast.makeText(requireContext(), "Đã thêm yêu thích", Toast.LENGTH_SHORT).show()
                    },
                    onRemoveFavorite = { productId ->
                        // Ví dụ: xóa sản phẩm khỏi danh sách yêu thích
                        Toast.makeText(requireContext(), "Đã xóa yêu thích", Toast.LENGTH_SHORT).show()
                    }
                )

                binding.rvProducts.adapter = productAdapter

                // Sau khi gán adapter, mới cho phép search
                binding.edtSearch.isEnabled = true
                binding.edtSearch.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        val query = s.toString().trim()
                        productAdapter.filter(query)
                        
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                })
            },
            onFailure = { e ->
                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Tránh memory leak, gỡ binding khi view bị hủy
        _binding = null
    }
}
