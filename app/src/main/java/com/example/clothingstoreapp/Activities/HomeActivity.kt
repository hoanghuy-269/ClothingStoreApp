package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
        // Lấy danh sách sản phẩm từ repository và thiết lập RecyclerView
        ProductRepository.getAllProducts(
            onSuccess = { productList ->
                binding.rvProducts.layoutManager = GridLayoutManager(context, 2)
                binding.rvProducts.adapter = ProductAdapter(productList) { product ->
                    val intent = Intent(requireContext(), SignInActivity::class.java)
                    startActivity(intent)
                }
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
