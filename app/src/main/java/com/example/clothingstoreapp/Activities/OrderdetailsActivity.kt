package com.example.clothingstoreapp.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.Adapter.OrderAdapter
import com.example.clothingstoreapp.Model.Orderdetails
import com.google.firebase.firestore.FirebaseFirestore
import com.example.clothingstoreapp.databinding.ActivityOrderdetailsBinding
import com.google.firebase.auth.FirebaseAuth

class OrderdetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderdetailsBinding
    private lateinit var firestore: FirebaseFirestore
    private var allOrdersList = mutableListOf<Orderdetails>()  // Lưu trữ tất cả đơn hàng
    private var filteredOrdersList = mutableListOf<Orderdetails>()  // Lưu trữ đơn hàng đã lọc theo trạng thái

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Lấy userId từ Firebase Authentication
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            fetchOrderDetails(it)  // Gọi hàm fetchOrderDetails để lấy tất cả đơn hàng của người dùng
        } ?: run {
            Toast.makeText(this, "Không tìm thấy người dùng.", Toast.LENGTH_SHORT).show()
        }

        // Thiết lập Spinner
        setupSpinner()
    }

    private fun setupSpinner() {
        val spinner: Spinner = binding.spinnerStatus

        // Tạo danh sách các trạng thái đơn hàng
        val statusList = listOf("Tất cả", "Chờ vận chuyển", "Đang vận chuyển", "Đã vận chuyển")

        // Thiết lập Adapter cho Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Lắng nghe sự kiện chọn mục trong Spinner
        spinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: android.widget.AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d("SpinnerSelected", "Status selected: ${statusList[position]}")
                    filterOrdersByStatus(position)  // Gọi hàm lọc đơn hàng theo trạng thái
                }

                override fun onNothingSelected(parentView: android.widget.AdapterView<*>) {
                    // Xử lý khi không có gì được chọn (nếu cần)
                }
            }
    }

    // Lọc đơn hàng theo trạng thái trong Spinner
    private fun filterOrdersByStatus(statusPosition: Int) {
        filteredOrdersList.clear()
        when (statusPosition) {
            0 -> filteredOrdersList.addAll(allOrdersList)  // "Tất cả"
            1 -> filteredOrdersList.addAll(allOrdersList.filter { it.status == "Chờ vận chuyển" })
            2 -> filteredOrdersList.addAll(allOrdersList.filter { it.status == "Đang vận chuyển" })
            3 -> filteredOrdersList.addAll(allOrdersList.filter { it.status == "Đã vận chuyển" })
        }

        updateRecyclerView()  // Cập nhật RecyclerView với dữ liệu đã lọc
    }

    private fun updateRecyclerView() {
        if (filteredOrdersList.isEmpty()) {
            binding.recyclerViewOrders.visibility = View.GONE
            binding.noOrdersMessage.visibility =
                View.VISIBLE  // Hiển thị thông báo nếu không có đơn hàng
        } else {
            binding.recyclerViewOrders.visibility = View.VISIBLE
            binding.noOrdersMessage.visibility = View.GONE
            val orderAdapter =
                OrderAdapter(filteredOrdersList)  // Cập nhật RecyclerView với dữ liệu đã lọc
            binding.recyclerViewOrders.layoutManager = LinearLayoutManager(this)
            binding.recyclerViewOrders.adapter = orderAdapter
        }
    }

    private fun fetchOrderDetails(userId: String) {
        firestore.collection("orders")  // Collection orders
            .whereEqualTo("userId", userId)  // Lọc theo userId để lấy tất cả đơn hàng của người dùng
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    binding.noOrdersMessage.visibility = View.VISIBLE  // Hiển thị thông báo nếu không có đơn hàng
                    Log.d("Orderdetails", "Không có đơn hàng nào.")
                } else {
                    // Duyệt qua từng document
                    documents.forEach { document ->
                        val orderItems = document.get("items") as? List<Map<String, Any>>  // Lấy mảng items từ document
                        orderItems?.forEach { item ->
                            val order = Orderdetails(
                                productName = item["name"] as? String ?: "",  // Lấy tên sản phẩm
                                price = item["price"] as? Double ?: 0.0,      // Lấy giá sản phẩm
                                quantity = item["quantity"] as? Int ?: 0,     // Lấy số lượng
                                size = item["selectedSize"] as? String ?: "M", // Lấy kích thước, mặc định "M"
                                status = item["status"] as? String ?: "",      // Lấy trạng thái
                                totalPrice = (item["price"] as? Double ?: 0.0) * (item["quantity"] as? Int ?: 0), // Tính tổng giá trị đơn hàng
                                productImage = "",  // Nếu bạn có trường productImage, bạn cần lấy từ Firestore
                                returnPolicy = item["returnPolicy"] as? String ?: "Không có chính sách"  // Truyền giá trị cho returnPolicy
                            )


                            allOrdersList.add(order)  // Thêm vào danh sách tất cả đơn hàng
                        }
                    }

                    // Cập nhật RecyclerView với dữ liệu đã lấy
                    updateRecyclerView()
                }
            }
            .addOnFailureListener { e ->
                Log.e("OrderdetailsActivity", "Lỗi khi lấy đơn hàng: ", e)
                Toast.makeText(this, "Không thể lấy dữ liệu đơn hàng.", Toast.LENGTH_SHORT).show()
            }
    }
}
