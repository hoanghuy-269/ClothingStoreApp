package com.example.clothingstoreapp.Activities

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

class OnboardingScreenLayout : AppCompatActivity() {


    private lateinit var binding : OnboardingScreenLayoutBinding
    private val slides = listOf(
        Slides(R.drawable.img_welcome_home,"Seamles Shopping Experience" ,"Shop easy, fast, and hassle-free"),
        Slides(R.drawable.img_item_wishlist,"Wishlist : Where Fashion Dreams Begin","Start your style journey with Wishlist"),
        Slides(R.drawable.img_welcome_cart,"Swift and Reliable Delivery","Fast, secure, and always on time")
    )
    private var currentIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // khởi tạo binding
        binding = OnboardingScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupDots()
        selectDot(currentIndex)

        // adapter
        val adapter = SlideAdapter(slides)
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter


        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            // hàm gọi cuộn theo trục dx ,dy
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // trả về chỉ số index của item đầu tiên
                val layoutManager = recyclerView.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager
                currentIndex = layoutManager?.findFirstVisibleItemPosition() ?: 0
                selectDot(currentIndex)
            }
        })
        binding.btnLeft.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex -= 1
                val layoutManager = binding.recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.scrollToPosition(currentIndex)
                selectDot(currentIndex)
            }
        }

        binding.btnRight.setOnClickListener {
            if (currentIndex < slides.size - 1) {
                currentIndex += 1
                val layoutManager = binding.recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.scrollToPosition(currentIndex)
                selectDot(currentIndex)
            }
        }
    }
    private fun setupDots() {
        // b1 : tạo đối tượng chứa các silde
        val dots = slides.size
        // b2 : loại bỏ các viewcon
        binding.layoutDots.removeAllViews()
        //b3 : khởi tạo vòng lập for chạy tương ứng với slide
        for (i in 0 until dots) {
            // khởi tọa một imagvew mới
            val dot = ImageView(this).apply {
                setImageResource(R.drawable.around) // trường hợp mặc định

                // tạo đối tượng dot tương ứng với ảnh
                val params = LinearLayout.LayoutParams(30, 30)
                params.setMargins(8, 0, 8, 0)
                layoutParams = params
            }
            binding.layoutDots.addView(dot)
        }
    }

    // đổi màu các dấu dot
    private fun selectDot(index: Int) {
        // childcout thuộc tính viewGroup dùng để số view con
        for (i in 0 until binding.layoutDots.childCount) {
            val dot = binding.layoutDots.getChildAt(i) as ImageView
            if (i == index) {
                dot.setImageResource(R.drawable.ic_around_brown) // selected
            } else {
                dot.setImageResource(R.drawable.around) // unselected
            }
        }
    }
}