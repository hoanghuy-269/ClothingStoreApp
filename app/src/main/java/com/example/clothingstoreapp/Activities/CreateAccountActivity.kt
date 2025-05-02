package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.CreateAccoutLayoutBinding
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding : CreateAccoutLayoutBinding
    private lateinit var mAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateAccoutLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // khởi tạo
        mAuth = FirebaseAuth.getInstance()
        setEvent()
    }
    private fun setEvent()
    {

        binding.btnSignUp.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val name = binding.edtName.text.toString().trim()
            if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            {
                Toast.makeText(this, " Vui lòng điền đầy dủ các thông tin ",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task ->

                    if (task.isSuccessful)
                    {
                        val user = mAuth.currentUser

                        // cập nhat them name
                        val profile = user?.let {
                            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()
                        }
                        user?.updateProfile(profile!!)
                            ?.addOnCompleteListener { updateTask ->
                                if(updateTask.isSuccessful)
                                {
                                    Toast.makeText(this," Đăng kí thành công",Toast.LENGTH_LONG).show()
                                    val intent = Intent(this,SignInActivity::class.java)
                                    startActivity(intent)
                                }
                            }

                    }
                    else
                    {
                        Toast.makeText(this, "Đăng kí thất bại: ${task.exception?.message}",Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}