package com.afaryn.kaoslab.ui_owner.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentOrderDetailsBinding


class OrderDetailsFragment : Fragment() {
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        populateOrderDetails()
        setupButtons()
    }

    private fun setupToolbar() {
        binding.backArrow.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun populateOrderDetails() {
    }

    private fun setupButtons() {
        binding.arrangeShipmentButton.setOnClickListener {
            // Logika untuk mengelola pengiriman
        }

        binding.downloadButton.setOnClickListener {
            // Logika untuk mengunduh detail pesanan atau invoice
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}