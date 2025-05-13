package com.example.clothingstoreapp.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingstoreapp.Adapter.SlideAdapter
import com.example.clothingstoreapp.Model.Slides

import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.databinding.OnboardingScreenLayoutBinding

class OnboardingScreenActivity : AppCompatActivity() {

    private lateinit var binding : OnboardingScreenLayoutBinding
    private lateinit var layoutManager: LinearLayoutManager
    private val slides = listOf(
        Slides(R.drawable.img_modelcart,"Welcome to ClothingStore" ,"Shop fashion easily, quickly, and hassle-free"),
        Slides(R.drawable.img_welcome_home,"Your Fashion Wishlist ","Save your favorite items and never miss a trend!"),
        Slides(R.drawable.img_shoppingonline,"Fast & Reliable Delivery","One-tap checkout, doorstep delivery!")
    )

    private var currentIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // khởi tạo binding
        binding = OnboardingScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        thietLapRecycleView()
        thietLapDieuHuong()
        thietlapDots()
        capnhatDots(currentIndex)
    }
    private fun thietLapRecycleView(){
        layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = SlideAdapter(slides)
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentIndex = layoutManager.findFirstVisibleItemPosition()
                capnhatDots(currentIndex)
            }
        })

    }
    private fun thietLapDieuHuong(){
        binding.btnLeft.setOnClickListener{backSlide()}
        binding.btnRight.setOnClickListener { nextSlide() }
    }

    private fun backSlide()
    {
        if(currentIndex > 0)
        {
            currentIndex --
            binding.recyclerView.smoothScrollToPosition(currentIndex)
            capnhatDots(currentIndex)
        }
    }
    private fun nextSlide()
    {
        if(currentIndex < slides.size - 1)
        {
            currentIndex ++
            binding.recyclerView.smoothScrollToPosition(currentIndex)
            capnhatDots(currentIndex)
        }else
        {
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }
    }
    private fun thietlapDots()
    {
        binding.layoutDots.removeAllViews()
        slides.forEach { _->
            val dot = ImageView(this).apply {
                setImageResource(R.drawable.around)
                layoutParams = LinearLayout.LayoutParams(30,30).apply {
                    setMargins(8,0,8,0)
                }
            }
        }
    }
    private fun capnhatDots(selectedIndex:Int)
    {
        for(i in 0 until binding.layoutDots.childCount)
        {
            val dot = binding.layoutDots.getChildAt(i) as ImageView
            dot.setImageResource(
                if(i == selectedIndex) R.drawable.ic_around_brown else R.drawable.around
            )
        }
    }

}