package com.example.clothingstoreapp.Activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.clothingstoreapp.databinding.HomeLayoutBinding

class HomeActivity : Fragment() {

    private var _binding: HomeLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Khởi tạo ViewBinding
        _binding = HomeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sử dụng binding để xử lý sự kiện hoặc thao tác với UI
        binding.imgBanner.setOnClickListener {
            // Xử lý khi người dùng nhấn nút
            binding.txtAddress.text = "Bạn đã nhấn nút!"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Tránh memory leak, gỡ binding khi view bị hủy
        _binding = null
    }
}
