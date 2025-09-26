package com.afaryn.kaoslab.presentation.ui_designer.seller_centre

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentSellerCentreBinding
import com.afaryn.kaoslab.utils.hideBottomNavDesigner

class SellerCentreFragment : Fragment() {

    private var _binding: FragmentSellerCentreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavDesigner()
        _binding = FragmentSellerCentreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupActions()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupActions() {
        binding.btnYourDesign.setOnClickListener {
            findNavController().navigate(R.id.action_sellerCentreFragment_to_designListFragment)
        }

        binding.btnPortfolio.setOnClickListener {
            findNavController().navigate(R.id.action_sellerCentreFragment_to_portfolioListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}