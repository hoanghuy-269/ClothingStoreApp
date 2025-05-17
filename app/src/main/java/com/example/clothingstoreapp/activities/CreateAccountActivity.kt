package com.example.clothingstoreapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.models.User
import com.example.clothingstoreapp.databinding.CreateAccoutLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var binding: CreateAccoutLayoutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // lưu trữ hàng số dễ bảo trì
    companion object {
        private const val USER_COLLECTION = "users"
        private const val MIN_PASSWORD_LENGTH = 6
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateAccoutLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding.btnSignUp.setOnClickListener {
            xuLiDangKi()
        }

    }
    private fun xuLiDangKi()
    {
        val name = binding.edtName.text.toString().trim()
        val phone = binding.edtPhone.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        if(!kiemTra(name, phone, email, password)) return
        showLoading(true)
        auth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                luuNguoiDungVaoFireBase(name,phone,email)
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                showToast("Đăng kí thất bại :{${exception.message}}")
            }
    }
    private fun kiemTra(name:String ,phone:String , email:String , password:String) : Boolean{
        if(name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()){
            showToast(" Vui lòng điền đầy đủ các trường thông tin ")
            return false
        }
        if(password.length < MIN_PASSWORD_LENGTH)
        {
            showToast(" Mật khẩu phải có ít nhất $MIN_PASSWORD_LENGTH ký tự")
            return false
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showToast("Email không đúng định dạng ")
            return false
        }
        if(!phone.matches(Regex("\\d{10}")))
        {
            showToast(" Số Điện thoại không hợp lệ (10 kí tự )")
        }
        return true
    }
    private fun luuNguoiDungVaoFireBase(name: String,phone: String,email: String){

        val uid = auth.currentUser?.uid ?: return

        val user = User(
            uid = uid,
            name = name,
            phone = phone,
            email = email,
            gender = null,
            avatarURI = null,
            address = "",
            role = ""
        )
        db.collection(USER_COLLECTION).document(uid).set(user)
            .addOnSuccessListener {
                showToast("Đăng ký thành công")
                startActivity(Intent(this,SignInActivity::class.java))
                finish()
            }
            .addOnFailureListener { exeption ->
                showLoading(false)
                showToast(" lỗi lưu trữ  ${exeption.message}")
            }
    }
    private fun showLoading(isLoading: Boolean)
    {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignUp.isEnabled = !isLoading
    }

    private fun showToast(message:String)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
}
