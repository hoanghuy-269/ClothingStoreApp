package com.example.clothingstoreapp.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.NewPasswordLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class NewPasswordActivity : AppCompatActivity() {
    private lateinit var binding: NewPasswordLayoutBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewPasswordLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()



        // Xử lý sự kiện lưu mật khẩu mới
        binding.btnCreateNewPassword.setOnClickListener {
            val newPassword = binding.newPassword.text.toString()
            val confirmPassword = binding.ComfirmPassword.text.toString()

            // Kiểm tra mật khẩu mới và xác nhận mật khẩu có khớp không
            if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (newPassword == confirmPassword) {
                    updatePassword(newPassword)
                } else {
                    Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Cập nhật mật khẩu mới vào Firebase
    private fun updatePassword(newPassword: String) {
        val user: FirebaseUser? = mAuth.currentUser
        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Mật khẩu đã được cập nhật thành công", Toast.LENGTH_SHORT).show()
                        finish() // Đóng màn hình NewPasswordActivity
                    } else {
                        Toast.makeText(this, "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
        }
    }
}
