package com.example.clothingstoreapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
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

    private fun searchProduct() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //TODO("Not yet implemented")
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                adapter.filter(query)
            }
        })


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
                showDeleteConfirmationDialog(product)
            }
        )
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewProducts.adapter = adapter
    }

    private fun loadProducts() {
        ProductRepository.getAllProducts(
            onSuccess = { products ->
                adapter.submitList(products)
                searchProduct()
            },
            onFailure = { e ->
                Toast.makeText(context, "Lỗi khi tải sản phẩm: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        )
    }
    private fun showDeleteConfirmationDialog(product: Product) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm \"${product.name}\" không?")
            .setPositiveButton("Xóa") { dialog, _ ->
                deleteProduct(product)
                dialog.dismiss()
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteProduct(product: Product) {
        ProductRepository.deleteProduct(
            productId = product.id,
            onSuccess = {
                Toast.makeText(context, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show()
                loadProducts()
            },
            onFailure = { e ->
                Toast.makeText(context, "Lỗi khi xóa sản phẩm: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
