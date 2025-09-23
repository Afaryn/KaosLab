package com.afaryn.kaoslab.ui_owner.sales.my_sales

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.databinding.FragmentSalesTabBinding
import com.afaryn.kaoslab.model.Order
import com.afaryn.kaoslab.utils.Constants.DELIVERED_STATUS
import com.afaryn.kaoslab.utils.Constants.PENDING_STATUS
import com.afaryn.kaoslab.utils.Constants.PROCESSING_STATUS
import com.afaryn.kaoslab.utils.Constants.SHIPPED_STATUS
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SalesTabFragment : Fragment() {

    private var _binding: FragmentSalesTabBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MySalesViewModel by activityViewModels()
    private lateinit var salesAdapter: SalesAdapter
    private var orderStatus: String = ""

    companion object {
        private const val ARG_STATUS = "status"

        fun newInstance(status: String): SalesTabFragment {
            val fragment = SalesTabFragment()
            val args = Bundle()
            args.putString(ARG_STATUS, status)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderStatus = arguments?.getString(ARG_STATUS) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()

        // Load data for this specific status
        viewModel.loadOrdersByStatus(orderStatus)
    }

    private fun setupRecyclerView() {
        salesAdapter = SalesAdapter(
            onArrangeShipment = { order ->
                navigateToChooseCourier(order.orderId)
            },
            onSeeDetails = { order ->
                navigateToOrderDetails(order.orderId)
            },
            onContactCustomer = { order, phone ->
                contactCustomerViaWhatsApp(order)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = salesAdapter
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            when (orderStatus) {
                "unpaid" -> {
                    viewModel.unpaidOrders.collect { response ->
                        handleResponse(response)
                    }
                }
                "to_deliver" -> {
                    viewModel.toDeliverOrders.collect { response ->
                        handleResponse(response)
                    }
                }
                "shipping" -> {
                    viewModel.shippingOrders.collect { response ->
                        handleResponse(response)
                    }
                }
                "completed" -> {
                    viewModel.completedOrders.collect { response ->
                        handleResponse(response)
                    }
                }
            }
        }
    }

    private fun handleResponse(response: Response<List<Order>>) {
        when (response) {
            is Response.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.GONE
            }
            is Response.Success -> {
                binding.progressBar.visibility = View.GONE
                if (response.data.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                    salesAdapter.submitList(response.data)
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                }
            }
            is Response.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
                // You can show error message here
            }
            is Response.Idle -> {
                // Initial state
            }
        }
    }

    private fun navigateToChooseCourier(orderId: String) {
        val action = MySalesFragmentDirections
            .actionMySalesFragmentToChooseCourierFragment(orderId)
        findNavController().navigate(action)
    }

    private fun navigateToOrderDetails(orderId: String) {
        val action = MySalesFragmentDirections
            .actionMySalesFragmentToOrderDetailsFragment(orderId)
        findNavController().navigate(action)
    }

    private fun contactCustomerViaWhatsApp(order: Order) {
        // Get customer phone number from the order's customer data
        viewModel.getUserById(order.customerId).observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Success -> {
                    val customerPhone = response.data.phone
                    if (customerPhone.isNotEmpty()) {
                        val message = "Hello ${order.customerName}, regarding your order ${order.orderId}..."
                        val uri = Uri.parse("https://wa.me/$customerPhone?text=${Uri.encode(message)}")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            startActivity(intent)
                        } catch (e: Exception) {
                            toast("WhatsApp is not installed")
                        }
                    } else {
                        toast("Customer phone number not available")
                    }
                }
                is Response.Error -> {
                    toast("Unable to get customer information")
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
