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
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import com.example.clothingstoreapp.Model.OrderItem
import com.example.clothingstoreapp.Model.Product

class ProductdetailsMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductdetailsMainBinding
    private var selectedSize: String? = null // Lưu trữ size đã chọn
    private var product: Product? = null // Lưu trữ thông tin sản phẩm

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

        // Xử lý khi nhấn nút "Add to Cart"
        // Xử lý khi nhấn nút "Add to Cart"
        binding.addToCartButton.setOnClickListener {
            // Sử dụng size mặc định nếu không có size được chọn
            val sizeToAdd = selectedSize ?: "M"  // Mặc định chọn size "M" nếu không có size

            if (product != null) {
                // Tạo đối tượng OrderItem với thông tin sản phẩm đã có
                val orderItem = OrderItem(
                    productId = product?.id ?: "", // Dùng id sản phẩm đã tải về
                    productName = product?.name ?: "", // Tên sản phẩm
                    price = product?.price?.toDouble() ?: 0.0, // Giá sản phẩm
                    quantity = 1,
                    size = sizeToAdd // Sử dụng size mặc định hoặc size đã chọn
                )

                val db = FirebaseFirestore.getInstance()
                val orderRef = db.collection("orders").document("current_cart")

                orderRef.get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val existingOrder =
                            snapshot.toObject(com.example.clothingstoreapp.Model.Order::class.java)
                        val items = existingOrder?.items?.toMutableList() ?: mutableListOf()

                        // Kiểm tra nếu sản phẩm đã có trong giỏ hàng (cùng size)
                        val matchedIndex = items.indexOfFirst {
                            it.productId == orderItem.productId && it.size == orderItem.size
                        }

                        if (matchedIndex != -1) {
                            items[matchedIndex].quantity += 1 // Nếu có thì cộng thêm 1 vào quantity
                        } else {
                            items.add(orderItem) // Nếu chưa có, thêm mới
                        }

                        // Cập nhật giỏ hàng trong Firestore
                        orderRef.update("items", items)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Đã cập nhật giỏ hàng", Toast.LENGTH_SHORT)
                                    .show()
                                // Chuyển màn hình sang CartActivity
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("SHOW_CART_FRAGMENT", true) // Đặt flag để MainActivity biết rằng phải hiển thị CartFragment
                                startActivity(intent)// Chuyển sang màn hình giỏ hàng
                            }

                    } else {
                        // Tạo mới giỏ hàng nếu chưa có
                        val newOrder = com.example.clothingstoreapp.Model.Order(
                            userId = "user_01", // Hoặc lấy từ FirebaseAuth
                            orderDate = System.currentTimeMillis().toString(),
                            status = "pending",
                            totalAmount = orderItem.price,
                            items = listOf(orderItem)
                        )

                        // Lưu giỏ hàng mới vào Firestore
                        orderRef.set(newOrder)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT)
                                    .show()
                                // Chuyển màn hình sang CartActivity
                                startActivity(
                                    Intent(
                                        this,
                                        CartActivity::class.java
                                    )
                                ) // Chuyển sang màn hình giỏ hàng
                            }
                    }
                }
            }
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
                    this.product = product // Lưu thông tin sản phẩm vào biến product
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
                            selectedSize = size // Lưu lại size đã chọn
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
