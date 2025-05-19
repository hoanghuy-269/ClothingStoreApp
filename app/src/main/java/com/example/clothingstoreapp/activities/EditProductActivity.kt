package com.example.clothingstoreapp.activities

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.cloudinary.CloudinaryConfig
import com.example.clothingstoreapp.databinding.EditProductLayoutBinding
import com.example.clothingstoreapp.models.Product
import com.example.clothingstoreapp.repository.ProductRepository
import com.google.firebase.Timestamp
import java.io.File
import java.io.FileOutputStream

class EditProductActivity : AppCompatActivity() {
    private lateinit var binding: EditProductLayoutBinding
    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null
    private lateinit var product: Product
    private val TAG = "EditProductActivity"

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            Log.d(TAG, "Image selected: $it")
            Glide.with(this)
                .load(it)
                .centerCrop()
                .into(binding.imgProduct)
            uploadProductImage(it,
                onFailure = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Upload failed: $error")
                },
                onSuccess = { url ->
                    uploadedImageUrl = url
                    Log.d(TAG, "Upload success: $url")
                })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditProductLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "Binding initialized: btnSaveProduct=${binding.btnSaveProduct}")
        CloudinaryConfig.init(this)

        val productId = intent.getStringExtra(EXTRA_PRODUCT_ID)
        if (productId == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        loadProduct(productId)
        setupListeners()
    }

    private fun loadProduct(productId: String) {
        ProductRepository.getProductById(
            productId = productId,
            onSuccess = { fetchedProduct ->
                product = fetchedProduct
                populateFields()
            },
            onFailure = { e ->
                Toast.makeText(this, "Lỗi khi tải sản phẩm: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Load product failed: ${e.message}")
                finish()
            }
        )
    }

    private fun populateFields() {
        binding.etProductName.setText(product.name)
        binding.etPrice.setText(product.price.toString())
        binding.etStock.setText(product.stock.toString())
        binding.etDescription.setText(product.description)
        binding.etCategoryId.setText(product.categoryId)
        binding.etRating.setText(product.rating.toString())
        binding.etSizes.setText(product.sizes.joinToString(", "))
        uploadedImageUrl = product.images
        if (!product.images.isNullOrEmpty()) {
            Glide.with(this)
                .load(product.images)
                .centerCrop()
                .placeholder(R.drawable.profile)
                .into(binding.imgProduct)
        }
    }

    private fun setupListeners() {
        binding.imgProduct.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSaveProduct.setOnClickListener {
            Log.d("edit", "Save button clicked")
            saveProduct()
        }

        binding.btnSaveProduct.setOnTouchListener { _, event ->
            Log.d("edit", "Save button touched: ${event.action}")
            false
        }

        binding.buttonContainer.setOnClickListener {
            Log.d("edit", "Button container clicked")
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString().trim()
        val price = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0
        val description = binding.etDescription.text.toString().trim()
        val stock = binding.etStock.text.toString().toIntOrNull() ?: 0
        val categoryId = binding.etCategoryId.text.toString().trim()
        val rating = binding.etRating.text.toString().toFloatOrNull() ?: 0f
        val sizes = binding.etSizes.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }

        Log.d(TAG, "Input: name=$name, price=$price, description=$description, stock=$stock, categoryId=$categoryId, rating=$rating, sizes=$sizes, imageUrl=$uploadedImageUrl")

        if (!validateProductInput(name, price, description, stock, categoryId)) {
            return
        }

        val updatedProduct = product.copy(
            name = name,
            price = price,
            description = description,
            stock = stock,
            categoryId = categoryId,
            images = uploadedImageUrl ?: product.images,
            rating = rating,
            sizes = if (sizes.isNotEmpty()) sizes else product.sizes,
            createdAt = product.createdAt,
        )

        binding.btnSaveProduct.isEnabled = false
        ProductRepository.updateProduct(
            product = updatedProduct,
            onSuccess = {
                Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Product updated successfully")
                setResult(RESULT_OK)
                finish()
            },
            onFailure = { e ->
                Toast.makeText(this, "Lỗi khi cập nhật sản phẩm: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Update product failed: ${e.message}")
                binding.btnSaveProduct.isEnabled = true
            }
        )
    }

    private fun validateProductInput(
        name: String,
        price: Double,
        description: String,
        stock: Int,
        categoryId: String
    ): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Validation failed: Empty name")
            return false
        }
        if (price <= 0) {
            Toast.makeText(this, "Giá sản phẩm phải lớn hơn 0", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Validation failed: Invalid price")
            return false
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mô tả sản phẩm", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Validation failed: Empty description")
            return false
        }
        if (stock < 0) {
            Toast.makeText(this, "Số lượng tồn kho không hợp lệ", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Validation failed: Invalid stock")
            return false
        }
        if (categoryId.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập danh mục sản phẩm", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Validation failed: Empty categoryId")
            return false
        }
        return true
    }

    private fun uploadProductImage(uri: Uri, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val file = File(cacheDir, "temp_product_${System.currentTimeMillis()}.jpg")
        try {
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            MediaManager.get().upload(file.path)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d(TAG, "Upload started: $requestId")
                    }
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val url = resultData?.get("secure_url") as? String
                        if (!url.isNullOrEmpty()) {
                            onSuccess(url)
                        } else {
                            onFailure("Không lấy được đường dẫn ảnh")
                        }
                        file.delete()
                    }
                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        onFailure("Lỗi upload: ${error?.description}")
                        file.delete()
                    }
                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                })
                .dispatch()
        } catch (e: Exception) {
            onFailure("Lỗi xử lý ảnh: ${e.message}")
            file.delete()
        } catch (e: IllegalStateException) {
            onFailure("Cloudinary chưa được khởi tạo: ${e.message}")
            file.delete()
        }
    }
}