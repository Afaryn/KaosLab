package com.afaryn.kaoslab.presentation.ui_owner.shop_performance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.databinding.FragmentShopPerformanceBinding
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopPerformanceFragment : Fragment() {

    private var _binding: FragmentShopPerformanceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShopPerformanceViewModel by viewModels()
    private lateinit var adapter: MonthlySalesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavOwner()
        _binding = FragmentShopPerformanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeMonthlySales()
    }

    private fun setupRecyclerView() {
        adapter = MonthlySalesAdapter { monthlySales ->
            // Navigate to month detail screen
            val action = ShopPerformanceFragmentDirections
                .actionShopPerformanceFragmentToMonthDetailFragment(
                    month = monthlySales.month,
                    year = monthlySales.year
                )
            findNavController().navigate(action)
        }
        binding.rvMonthlySales.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ShopPerformanceFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeMonthlySales() {
        lifecycleScope.launch {
            viewModel.monthlySales.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.layoutEmpty.visibility = View.GONE
                        binding.rvMonthlySales.visibility = View.GONE
                    }
                    is Response.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (response.data.isEmpty()) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                            binding.rvMonthlySales.visibility = View.GONE
                        } else {
                            binding.layoutEmpty.visibility = View.GONE
                            binding.rvMonthlySales.visibility = View.VISIBLE
                            adapter.submitList(response.data)
                        }
                    }
                    is Response.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.rvMonthlySales.visibility = View.GONE
                    }
                    else -> {
                        // Idle state
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
