package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.clothingstoreapp.adapter.ProductAdapter
import com.example.clothingstoreapp.databinding.HomeLayoutBinding
import com.example.clothingstoreapp.model.Product
import com.example.clothingstoreapp.repository.ProductRepository
import com.google.firebase.Timestamp

class HomeActivity : Fragment() {

    private var _binding: HomeLayoutBinding? = null
    private val binding get() = _binding!!

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

        // Xử lý sự kiện khi nhấn vào banner
        binding.imgBanner.setOnClickListener {
            // Thay đổi nội dung text
            val newProduct = Product(
                name = "Áo Thun Nam 2",
                description = "Áo thun cotton chất lượng cao",
                price = 200000,
                stock = 50,
                categoryId = "1",  // Ví dụ: Mã danh mục
                images = "a",
                createdAt = Timestamp.now(),
                rating = 0f
            )

            ProductRepository.addProduct(newProduct,
                onSuccess = {
                    // Hiển thị thông báo thành công
                    Toast.makeText(context, "Sản phẩm đã được thêm thành công!", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    // Hiển thị thông báo lỗi
                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

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
