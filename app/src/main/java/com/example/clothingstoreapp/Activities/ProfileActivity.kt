package com.example.clothingstoreapp.Activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.clothingstoreapp.Cloudinary.CloudinaryConfig
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.ProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : Fragment() {

    private lateinit var binding: ProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileBinding.inflate(inflater, container, false)
        CloudinaryConfig.init(requireContext()) // Khởi tạo Cloudinary
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kiểm tra nếu người dùng đã đăng nhập và lấy UID
        auth.currentUser?.uid?.let { uid ->
            loadAvatar(uid)
        } ?: run {
            Toast.makeText(context, "Không tìm thấy người dùng.", Toast.LENGTH_SHORT).show()
        }

        binding.imgYourProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileDetailActivity::class.java)
            startActivity(intent)
        }
        binding.imgMyOder.setOnClickListener {
            val intent = Intent(requireContext(), OrderActivity::class.java)
            startActivity(intent)
        }

        binding.imgLogOut.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("LogOut")
                .setMessage("Nhấn vào CÓ nếu bạn thoát ứng dụng.")
                .setPositiveButton("CÓ") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(requireContext(), "Đã thoát ứng dụng thành công", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Không", null)
                .show()
        }
        binding.imgMyOder.setOnClickListener {
            val intent = Intent(requireContext(), OrderdetailsActivity::class.java)
            startActivity(intent)
        }
    }


    private fun loadAvatar(uid: String) {
        if (!isAdded) return  // Dừng lại nếu Fragment chưa được gắn vào Activity

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val imageUrl = doc.getString("avatarURL")
                if (!imageUrl.isNullOrEmpty()) {
                    // Kiểm tra context trước khi gọi Glide
                    context?.let {
                        Glide.with(it).load(imageUrl).into(binding.imgLogo)
                    }
                } else {
                    binding.imgLogo.setImageResource(R.drawable.profile)
                }
            }
            .addOnFailureListener {
                showError("Lỗi tải ảnh: ${it.message}")
            }
    }

    private fun showError(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}
