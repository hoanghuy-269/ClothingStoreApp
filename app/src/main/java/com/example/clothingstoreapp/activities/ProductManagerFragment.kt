package com.example.clothingstoreapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        binding.fabAddProduct.setOnClickListener{
            startActivity(Intent(requireContext(), AddProductActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductManagerAdapter(
            onEditClick = {  },
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ProductManagerAdapter.REQUEST_CODE_EDIT_PRODUCT && resultCode == Activity.RESULT_OK) {
            loadProducts()
            Toast.makeText(context, "Sản phẩm đã được cập nhật", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}