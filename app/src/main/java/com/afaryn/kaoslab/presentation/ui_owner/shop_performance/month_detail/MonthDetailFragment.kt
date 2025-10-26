package com.afaryn.kaoslab.presentation.ui_owner.shop_performance.month_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.databinding.FragmentMonthDetailBinding
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MonthDetailFragment : Fragment() {

    private var _binding: FragmentMonthDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MonthDetailViewModel by viewModels()
    private val args: MonthDetailFragmentArgs by navArgs()
    private lateinit var adapter: MonthOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavOwner()
        _binding = FragmentMonthDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        setupClickListeners()
        observeOrders()

        // Load orders for the selected month
        viewModel.loadOrdersByMonth(args.month, args.year)
    }

    private fun setupUI() {
        binding.tvTitle.text = "${args.month} ${args.year}"
    }

    private fun setupRecyclerView() {
        adapter = MonthOrderAdapter()
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MonthDetailFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeOrders() {
        lifecycleScope.launch {
            viewModel.orders.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.layoutEmpty.visibility = View.GONE
                        binding.rvOrders.visibility = View.GONE
                    }
                    is Response.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (response.data.isEmpty()) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                            binding.rvOrders.visibility = View.GONE
                        } else {
                            binding.layoutEmpty.visibility = View.GONE
                            binding.rvOrders.visibility = View.VISIBLE
                            adapter.submitList(response.data)
                        }
                    }
                    is Response.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.rvOrders.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

