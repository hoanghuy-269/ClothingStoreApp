package com.example.clothingstoreapp.Activities

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.databinding.ActivityProductdetailsMainBinding
import com.example.clothingstoreapp.model.Product
import com.google.firebase.firestore.FirebaseFirestore

class ProductdetailsMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductdetailsMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductdetailsMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Quay lại màn hình trước
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Lấy ID sản phẩm từ Intent
        val productId = intent.getStringExtra("PRODUCT_ID")
        if (productId != null) {
            loadProductDetails(productId)
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProductDetails(productId: String) {
        // Lấy thông tin sản phẩm từ Firestore
        val db = FirebaseFirestore.getInstance()
        val productRef = db.collection("products").document(productId)

        productRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val product = document.toObject(Product::class.java)
                if (product != null) {
                    binding.tvProductName.text = product.name
                    binding.tvDescription.text = product.description
                    binding.tvPrice.text = "${product.price} đ"
                    binding.tvStock.text = "Stock: ${product.stock}"
                    binding.tvRating.text = "⭐ ${product.rating}"

                    Glide.with(this)
                        .load(product.images)
                        .into(binding.imageProduct)

                    // Hiển thị các nút chọn size (kiểm tra nếu sizes không null)
                    binding.sizeContainer.removeAllViews()
                    product.sizes?.forEach { size ->
                        val button = Button(this).apply {
                            text = size
                            setTextColor(ContextCompat.getColor(context, android.R.color.white))
                            setBackgroundColor(Color.parseColor("#6F4E37"))
                            textSize = 14f
                            layoutParams = ViewGroup.MarginLayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(16, 8, 16, 8)
                            }
                        }

                        button.setOnClickListener {
                            Toast.makeText(this, "Đã chọn size: $size", Toast.LENGTH_SHORT).show()
                        }

                        binding.sizeContainer.addView(button)
                    }

                } else {
                    // Sản phẩm không tồn tại trong cơ sở dữ liệu
                    Toast.makeText(this, "Không tìm thấy sản phẩm trong cơ sở dữ liệu", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Nếu không tìm thấy tài liệu
                Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            // Nếu có lỗi khi gọi Firestore
            Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
