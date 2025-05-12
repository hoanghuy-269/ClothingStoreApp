package com.example.clothingstoreapp.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.databinding.ActivityProductdetailsMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.clothingstoreapp.Model.CartItem
import com.example.clothingstoreapp.Model.Product
import com.example.clothingstoreapp.Model.Cart
import com.example.clothingstoreapp.R
import com.google.android.material.snackbar.Snackbar

class ProductdetailsMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductdetailsMainBinding
    private var selectedSize: String? = null
    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductdetailsMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val productId = intent.getStringExtra("PRODUCT_ID")
        if (productId != null) {
            loadProductDetails(productId)
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
        }

        binding.btnBuyNow.setOnClickListener {
            if (product != null && selectedSize != null) {
                val intent = Intent(this, OrderActivity::class.java).apply {
                    putExtra("productId", product?.id)
                    putExtra("productName", product?.name)
                    putExtra("productImage", product?.images)
                    putExtra("productPrice", product?.price)
                    putExtra("selectedSize", selectedSize)
                    putExtra("quantity", 1) // Giả sử mua 1 sản phẩm
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Vui lòng chọn kích thước và sản phẩm.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAddToCart.setOnClickListener {
            val sizeToAdd = selectedSize ?: "M"
            if (product != null) {
                val cartItem = CartItem(
                    productId = product?.id ?: "",
                    name = product?.name ?: "",
                    price = product?.price?.toDouble() ?: 0.0,
                    imageUrl = product?.images ?: "",
                    selectedSize = sizeToAdd,
                    quantity = 1
                )

                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val db = FirebaseFirestore.getInstance()
                    val cartRef = db.collection("carts").document(userId)

                    cartRef.get().addOnSuccessListener { snapshot ->
                        val items = snapshot.toObject(Cart::class.java)?.items?.toMutableList() ?: mutableListOf()

                        val matchedIndex = items.indexOfFirst {
                            it.productId == cartItem.productId && it.selectedSize == cartItem.selectedSize
                        }

                        if (matchedIndex != -1) {
                            items[matchedIndex].quantity += 1
                        } else {
                            items.add(cartItem)
                        }

                        cartRef.update("items", items)
                            .addOnSuccessListener {
                                showSnackbar("Thêm sản phẩm thành công")
                            }
                            .addOnFailureListener { e ->
                                Log.e("CartActivity", "Error updating cart: ${e.message}")
                                Toast.makeText(this, "Lỗi cập nhật: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }.addOnFailureListener { e ->
                        Log.e("CartActivity", "Error fetching cart: ${e.message}")
                        Toast.makeText(this, "Lỗi lấy giỏ hàng: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun loadProductDetails(productId: String) {
        val db = FirebaseFirestore.getInstance()
        val productRef = db.collection("products").document(productId)

        productRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                product = document.toObject(Product::class.java)
                binding.tvProductName.text = product?.name
                binding.tvPrice.text = "${product?.price} đ"
                binding.tvDescription.text = product?.description
                Glide.with(this).load(product?.images).into(binding.imageProduct)

                // Thiết lập kích thước sản phẩm
                binding.sizeContainer.removeAllViews()
                product?.sizes?.forEach { size ->
                    val button = Button(this).apply {
                        text = size
                        setOnClickListener {
                            selectedSize = size
                            Toast.makeText(this@ProductdetailsMainActivity, "Đã chọn kích thước: $size", Toast.LENGTH_SHORT).show()
                        }
                    }
                    binding.sizeContainer.addView(button)
                }
            } else {
                Toast.makeText(this, "Không tìm thấy sản phẩm trong cơ sở dữ liệu", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}