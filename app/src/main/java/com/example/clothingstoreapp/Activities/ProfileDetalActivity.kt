package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.clothingstoreapp.Cloudinary.CloudinaryConfig
import com.example.clothingstoreapp.databinding.ProfileLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.File
import java.io.FileOutputStream

class ProfileDetalActivity : AppCompatActivity() {

    private lateinit var binding: ProfileLayoutBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uploadToCloudinary(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CloudinaryConfig.init(this)

        auth.currentUser?.uid?.let { uid ->
            loadAvatar(uid)
            setupImagePicker()
            loadUserProfile(uid)
        } ?: run {
            Toast.makeText(this, "Không tìm thấy người dùng.", Toast.LENGTH_SHORT).show()
        }
        setupListeners()
    }

    private fun setupListeners() {
        binding.imgBack.setOnClickListener {
            super.onBackPressed()

        }
        binding.btnComplete.setOnClickListener {
            updateUserProfile()
        }
    }
    private fun setupImagePicker() {
        binding.imgLogo.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun loadAvatar(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val avatarUrl = doc.getString("avatarURL")
                if (!avatarUrl.isNullOrEmpty()) {
                    Glide.with(this).load(avatarUrl).into(binding.imgLogo)
                }
            }
    }
    private fun loadUserProfile(uid: String) {
        val genders = listOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spnGender.adapter = adapter

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val gender = document.getString("gender") ?: ""
                val phone = document.getString("phone") ?: ""
                val name = document.getString("name") ?: ""

                binding.edtName.setText(name)
                binding.edtPhone.setText(phone)

                val index = genders.indexOf(gender)
                if (index != -1) binding.spnGender.setSelection(index)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Không thể tải dữ liệu: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserProfile() {
        val name = binding.edtName.text.toString().trim()
        val phone = binding.edtPhone.text.toString().trim()
        val gender = binding.spnGender.selectedItem.toString()
        val uid = auth.currentUser?.uid ?: return

        val updates = mapOf(
            "name" to name,
            "phone" to phone,
            "gender" to gender
        )

        db.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            super.onBackPressed()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi cập nhật: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadToCloudinary(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "temp_avatar_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        MediaManager.get().upload(file.path)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Toast.makeText(this@ProfileDetalActivity, "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show()
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val url = resultData?.get("secure_url") as? String
                    if (!url.isNullOrEmpty()) {
                        saveImageUrlToFirestore(url)
                        Glide.with(this@ProfileDetalActivity).load(url).into(binding.imgLogo)
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Toast.makeText(this@ProfileDetalActivity, "Lỗi upload: ${error?.description}", Toast.LENGTH_SHORT).show()
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }

    private fun saveImageUrlToFirestore(url: String) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .set(mapOf("avatarURL" to url), SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Ảnh đã được lưu", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi lưu URL: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
