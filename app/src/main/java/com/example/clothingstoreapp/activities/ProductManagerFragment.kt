package com.example.clothingstoreapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.adapter.ProductManagerAdapter
import com.example.clothingstoreapp.databinding.FragmentProductManagerBinding
import com.example.clothingstoreapp.models.Product
import com.example.clothingstoreapp.repository.ProductRepository

class ProductManagerFragment : Fragment() {

    private var _binding: FragmentProductManagerBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductManagerAdapter

    // Launcher để nhận kết quả khi chỉnh sửa sản phẩm
    private val editProductLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadProducts()
            Toast.makeText(requireContext(), "Sản phẩm đã được cập nhật", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadProducts()
        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(requireContext(), AddProductActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductManagerAdapter(
            onEditClick = { product ->
                val intent = Intent(requireContext(), EditProductActivity::class.java).apply {
                    putExtra(EditProductActivity.EXTRA_PRODUCT_ID, product.id)
                }
                editProductLauncher.launch(intent)
            },
            onDeleteClick = { product ->
                deleteProduct(product)
            }
        )
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewProducts.adapter = adapter
    }

    private fun loadProducts() {
        ProductRepository.getAllProducts(
            onSuccess = { products ->
                adapter.submitList(products)
            },
            onFailure = { e ->
                Toast.makeText(context, "Lỗi khi tải sản phẩm: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun deleteProduct(product: Product) {
        ProductRepository.deleteProduct(
            productId = product.id,
            onSuccess = {
                Toast.makeText(context, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show()
                loadProducts()
            },
            onFailure = { e ->
                Toast.makeText(context, "Lỗi khi xóa sản phẩm: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
