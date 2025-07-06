package com.afaryn.kaoslab.ui_owner.sales.my_sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.databinding.FragmentSalesListBinding
import com.afaryn.kaoslab.ui_owner.sales.my_sales.data.SalesItem

class SalesListFragment : Fragment() {
    private var _binding: FragmentSalesListBinding? = null
    private val binding get() = _binding!!

    private var salesStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            salesStatus = it.getString(ARG_SALES_STATUS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSalesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadDummyData(salesStatus ?: "Unpaid") // Load data based on status, default to "Unpaid"
    }

    private fun setupRecyclerView() {
        binding.salesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            // You can set an adapter here initially with an empty list,
            // or directly set it after data is loaded.
            adapter = SalesAdapter(emptyList()) // Initialize with an empty list
        }
    }

    private fun loadDummyData(status: String) {
        // In a real app, you would fetch data from an API or database
        // based on the 'status'.
        val dummyData = when (status) {
            "Unpaid" -> listOf(
                SalesItem(
                    id = "1",
                    userName = "John Doe",
                    userAvatarUrl = null,
                    status = "Unpaid",
                    productName = "Short sleeve t-shirt",
                    productImageUrl = null,
                    productDetails = "Screen printing: Front only (A4)\nSize: L",
                    quantity = 5,
                    totalAmount = "Rp375.000"
                ),
                SalesItem(
                    id = "2",
                    userName = "Jane Smith",
                    userAvatarUrl = null,
                    status = "Unpaid",
                    productName = "Custom Mug",
                    productImageUrl = null,
                    productDetails = "Design: Logo A\nColor: White",
                    quantity = 2,
                    totalAmount = "Rp150.000"
                )
            )
            "To Deliver" -> listOf(
                SalesItem(
                    id = "3",
                    userName = "Alice Brown",
                    userAvatarUrl = null,
                    status = "To Deliver",
                    productName = "Long sleeve hoodie",
                    productImageUrl = null,
                    productDetails = "Size: M\nColor: Black",
                    quantity = 1,
                    totalAmount = "Rp200.000"
                )
            )
            "Shipping" -> listOf(
                SalesItem(
                    id = "4",
                    userName = "Bob Johnson",
                    userAvatarUrl = null,
                    status = "Shipping",
                    productName = "Canvas Tote Bag",
                    productImageUrl = null,
                    productDetails = "Design: Quote B\nMaterial: Canvas",
                    quantity = 3,
                    totalAmount = "Rp180.000"
                )
            )
            "Completed" -> listOf(
                SalesItem(
                    id = "5",
                    userName = "Charlie Davis",
                    userAvatarUrl = null,
                    status = "Completed",
                    productName = "Custom Phone Case",
                    productImageUrl = null,
                    productDetails = "Model: iPhone 13\nFinish: Matte",
                    quantity = 1,
                    totalAmount = "Rp120.000"
                )
            )
            else -> emptyList()
        }

        (binding.salesRecyclerView.adapter as? SalesAdapter)?.let {
            // For simplicity, we're creating a new adapter. In a real app, you might use DiffUtil for updates.
            binding.salesRecyclerView.adapter = SalesAdapter(dummyData)
        }
    }

    companion object {
        private const val ARG_SALES_STATUS = "sales_status"

        @JvmStatic
        fun newInstance(salesStatus: String) =
            SalesListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SALES_STATUS, salesStatus)
                }
            }
    }
}