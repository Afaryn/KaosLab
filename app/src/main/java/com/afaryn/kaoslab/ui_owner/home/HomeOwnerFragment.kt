package com.afaryn.kaoslab.ui_owner.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentHomeOwnerBinding
import com.afaryn.kaoslab.ui_owner.home.adapter.LastOrderAdapter
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.showBottomNavOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class HomeOwnerFragment : Fragment() {

    private var _binding: FragmentHomeOwnerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeOwnerViewModel by viewModels()
    private lateinit var lastOrderAdapter: LastOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeOwnerBinding.inflate(inflater, container, false)
        showBottomNavOwner()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeData()
    }

    private fun setupRecyclerView() {
        lastOrderAdapter = LastOrderAdapter { orderItem ->
            // Handle order item click - navigate to order details
            // You can implement navigation to order details here
        }

        binding.salesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = lastOrderAdapter
        }
    }

    private fun setupClickListeners() {
        // Navigate to Business Reports when clicking "See Details"
        binding.btnSeeDetail.setOnClickListener {
            findNavController().navigate(R.id.action_homeOwnerFragment_to_salesRevenueOwnerFragment)
        }

        // Navigate to My Sales when clicking "Sales Details" or "see all"
        binding.btnSeeDetailSales.setOnClickListener {
            findNavController().navigate(R.id.action_homeOwnerFragment_to_mySalesFragment)
        }

        binding.lastOrderSeeAll.setOnClickListener {
            findNavController().navigate(R.id.action_homeOwnerFragment_to_mySalesFragment)
        }
    }

    private fun observeData() {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

        // Observe sales revenue
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.salesRevenue.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        // Show loading state
                    }
                    is Response.Success -> {
                        val formattedRevenue = currencyFormat.format(response.data).replace("IDR", "Rp")
                        binding.tvSalesRevenue.text = formattedRevenue
                    }
                    is Response.Error -> {
                        binding.tvSalesRevenue.text = "Rp0"
                    }
                    is Response.Idle -> {
                        // Initial state
                    }
                }
            }
        }

        // Observe order status counts
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.orderStatusCounts.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        // Show loading state
                    }
                    is Response.Success -> {
                        val counts = response.data
                        binding.toShipCount.text = counts.toShip.toString()
                        binding.canceledCount.text = counts.cancelled.toString()
                        binding.returnCount.text = counts.returned.toString()
                        binding.reviewCount.text = counts.review.toString()
                    }
                    is Response.Error -> {
                        // Set default values
                        binding.toShipCount.text = "0"
                        binding.canceledCount.text = "0"
                        binding.returnCount.text = "0"
                        binding.reviewCount.text = "0"
                    }
                    is Response.Idle -> {
                        // Initial state
                    }
                }
            }
        }

        // Observe last orders
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.lastOrders.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        // Show loading state
                    }
                    is Response.Success -> {
                        lastOrderAdapter.submitList(response.data)
                    }
                    is Response.Error -> {
                        // Handle error - show empty list
                        lastOrderAdapter.submitList(emptyList())
                    }
                    is Response.Idle -> {
                        // Initial state
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}