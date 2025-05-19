package com.example.clothingstoreapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingstoreapp.databinding.SignInLayoutBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: SignInLayoutBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        intent.getStringExtra("email")?.let {
            binding.edtEmail.setText(it)
        }

        setupEvents()
    }

    private fun setupEvents() {
        binding.btnSignIn.setOnClickListener {

            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng không để trống", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showLoading(true)
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = mAuth.currentUser?.uid?: return@addOnSuccessListener
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { document->
                            val role = document.getString("role")
                            if(role == "admin"){

                                startActivity(Intent(this,AdminActivity::class.java))

///                             startActivity(Intent(this,MainActivity::class.java))
                            }
                            else if(role == "shipper")
                            {
                                startActivity(Intent(this,ShipperActivity::class.java))
                                finish()

                                startActivity(Intent(this,SucessOrderActivity::class.java))

                            }
                            else{
                                startActivity(Intent(this,MainActivity::class.java))
                            }
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "không tuy cập quyê người dùng", Toast.LENGTH_SHORT).show()
                        }

                }
                .addOnFailureListener {
                    Toast.makeText(this,"Sai Tài khoản hoặc mật khẩu",Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    showLoading(false)
                }
        }

        binding.txtSignUp.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }

        binding.txtForgotPassword.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, VerityCodeActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignIn.isEnabled = !isLoading
    }
}
