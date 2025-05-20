package com.example.clothingstoreapp.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.adapter.OrderManagerAdapter
import com.example.clothingstoreapp.databinding.FragmentOderManagerBinding
import com.example.clothingstoreapp.models.Order
import com.example.clothingstoreapp.repositories.OrderRepository

class OrderManagerFragment : Fragment() {

    private var _binding: FragmentOderManagerBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OrderManagerAdapter
    private lateinit var emptyMessage: TextView
    private var currentProgressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOderManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        loadOrders()
    }

    private fun setupViews() {
        emptyMessage = binding.emptyMessage

        adapter = OrderManagerAdapter(
            onEditClick = { order ->
                    showStatusUpdateDialog(order)
            },
            onDeleteClick = { order ->

                    showDeleteConfirmationDialog(order)
            }
        )

        binding.recyclerViewOrders.apply {
            adapter = this@OrderManagerFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun loadOrders() {
        showProgressDialog("Đang tải đơn hàng...")
            loadAllOrders()
    }

    private fun loadAllOrders() {
        OrderRepository.getAllOrders { orders ->
            dismissProgressDialog()
            handleOrdersResponse(orders)
        }
    }

    private fun handleOrdersResponse(orders: List<Order>) {
        if (orders.isEmpty()) {
            showEmptyState(true)
        } else {
            showEmptyState(false)
            adapter.submitList(orders)
        }
    }

    private fun showStatusUpdateDialog(order: Order) {
        try {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_update_status, null)
            val statusSpinner: Spinner = dialogView.findViewById(R.id.spinner_status)

            val statusOptions = arrayOf(
                "Pending",
                "Shipping",
                "Completed",
                "Cancelled",
            )

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statusOptions
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            statusSpinner.adapter = adapter
            statusSpinner.setSelection(statusOptions.indexOfFirst { it == order.status })

            AlertDialog.Builder(requireContext())
                .setTitle("Cập nhật trạng thái đơn hàng")
                .setView(dialogView)
                .setPositiveButton("Cập nhật") { _, _ ->
                    val newStatus = statusSpinner.selectedItem.toString()
                    updateOrderStatus(order, newStatus)
                }
                .setNegativeButton("Hủy", null)
                .show()
        } catch (e: Exception) {
            Log.e("OrderManagerFragment", "Error showing status dialog", e)
            showErrorDialog("Lỗi khi hiển thị hộp thoại cập nhật")
        }
    }

    private fun updateOrderStatus(order: Order, newStatus: String) {
        showProgressDialog("Updating...")

        order.userId?.let {
            OrderRepository.updateOrderStatus(
                userId = it,
                orderId = order.orderId,
                newStatus = newStatus
            ) { success ->
                dismissProgressDialog()

                if (success) {
                    showToast("Status update successful")
                    loadOrders()
                } else {
                    showErrorDialog("Status update fail")
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(order: Order) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm order deletion")
            .setMessage("Are you sure you want to delete the order #${order.orderId}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteOrder(order)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteOrder(order: Order) {
        showProgressDialog("Deleting order...")

        order.userId?.let {
            OrderRepository.deleteOrder(
                orderId = order.orderId,
                userId = it
            ) { success ->
                dismissProgressDialog()

                if (success) {
                    showToast("Order deleted successfully")
                    loadOrders()
                } else {
                    showErrorDialog("Xóa đơn hàng thất bại")
                }
            }
        }
    }

    private fun showProgressDialog(message: String) {
        currentProgressDialog?.dismiss()
        currentProgressDialog = ProgressDialog(requireContext()).apply {
            setMessage(message)
            setCancelable(false)
            show()
        }
    }

    private fun dismissProgressDialog() {
        currentProgressDialog?.dismiss()
        currentProgressDialog = null
    }

    private fun showEmptyState(show: Boolean) {
        emptyMessage.visibility = if (show) View.VISIBLE else View.GONE
        binding.recyclerViewOrders.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Lỗi")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentProgressDialog?.dismiss()
        _binding = null
    }
}