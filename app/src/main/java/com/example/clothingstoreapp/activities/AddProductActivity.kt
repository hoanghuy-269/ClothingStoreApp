package com.example.clothingstoreapp.activities

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.cloudinary.CloudinaryConfig
import com.example.clothingstoreapp.databinding.ActivityAddProductBinding
import com.example.clothingstoreapp.databinding.AddProductLayoutBinding
import com.example.clothingstoreapp.models.Product
import com.example.clothingstoreapp.repository.ProductRepository
import com.google.firebase.Timestamp
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: AddProductLayoutBinding
    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null
    private val TAG = "AddProductActivity"

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
                    binding.btnAddProduct.isEnabled = true
                    Log.d(TAG, "Upload success: $url")
                })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddProductLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CloudinaryConfig.init(this)
        setupListeners()
    }

    private fun setupListeners() {
        binding.imgProduct.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnAddProduct.setOnClickListener {
            Log.d("add", "Add button clicked")
            addProduct()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun addProduct() {
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

        val newProduct = Product(
            id = UUID.randomUUID().toString(),
            name = name,
            price = price,
            description = description,
            stock = stock,
            categoryId = categoryId,
            images = uploadedImageUrl ?: "",
            rating = rating,
            sizes = if (sizes.isNotEmpty()) sizes else listOf("S", "M", "L", "XL", "XXL", "XXXL"),
            createdAt = Timestamp.now()
        )

        binding.btnAddProduct.isEnabled = false // Vô hiệu hóa nút trong khi thêm
        ProductRepository.addProduct(
            product = newProduct,
            onSuccess = {
                Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Product added successfully")
                setResult(RESULT_OK)
                finish()
            },
            onFailure = { e ->
                Toast.makeText(this, "Lỗi khi thêm sản phẩm: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Add product failed: ${e.message}")
                binding.btnAddProduct.isEnabled = true // Bật lại nút nếu thất bại
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
            binding.etProductName.error = "Vui lòng nhập tên sản phẩm"
            Log.w(TAG, "Validation failed: Empty name")
            return false
        }
        if (price <= 0) {
            binding.etPrice.error = "Giá sản phẩm phải lớn hơn 0"
            Log.w(TAG, "Validation failed: Invalid price")
            return false
        }
        if (description.isEmpty()) {
            binding.etDescription.error = "Vui lòng nhập mô tả sản phẩm"
            Log.w(TAG, "Validation failed: Empty description")
            return false
        }
        if (stock < 0) {
            binding.etStock.error = "Số lượng tồn kho không hợp lệ"
            Log.w(TAG, "Validation failed: Invalid stock")
            return false
        }
        if (categoryId.isEmpty()) {
            binding.etCategoryId.error = "Vui lòng nhập danh mục sản phẩm"
            Log.w(TAG, "Validation failed: Empty categoryId")
            return false
        }
        if (uploadedImageUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Vui lòng chọn và tải ảnh sản phẩm", Toast.LENGTH_LONG).show()
            Log.w(TAG, "Validation failed: No image uploaded")
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