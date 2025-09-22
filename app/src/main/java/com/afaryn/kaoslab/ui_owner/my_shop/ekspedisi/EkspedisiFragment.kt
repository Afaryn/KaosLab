package com.afaryn.kaoslab.ui_owner.my_shop.ekspedisi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentEkspedisiBinding
import com.afaryn.kaoslab.databinding.FragmentHomeOwnerBinding
import com.afaryn.kaoslab.ui_owner.home.HomeOwnerViewModel
import com.afaryn.kaoslab.ui_owner.home.adapter.LastOrderAdapter
import com.afaryn.kaoslab.ui_owner.my_shop.ekspedisi.adapter.EkspedisiAdapter
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.hideBottomNavOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class EkspedisiFragment : Fragment() {

    private var _binding: FragmentEkspedisiBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EkspedisiViewModel by viewModels()
    private lateinit var ekspedisiAdapter: EkspedisiAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        hideBottomNavOwner()
        _binding = FragmentEkspedisiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        ekspedisiAdapter = EkspedisiAdapter { orderItem ->
            // Handle order item click - navigate to order details
            // You can implement navigation to order details here
        }

        binding.rvEkspedisi.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ekspedisiAdapter
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getListEkspedisi().observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Response.Loading -> {
                        // Show loading state
                    }
                    is Response.Success -> {
                        ekspedisiAdapter.submitList(response.data)
                    }
                    is Response.Error -> {
                        // Handle error - show empty list
                        ekspedisiAdapter.submitList(emptyList())
                    }
                    is Response.Idle -> {
                        // Initial state
                    }
                }
            }
        }
    }
}