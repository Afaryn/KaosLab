package com.afaryn.kaoslab.presentation.ui_designer.seller_centre

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentSellerCentreBinding
import com.afaryn.kaoslab.presentation.ui_designer.seller_centre.sales.DesignSalesActivity
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.Response
import com.afaryn.kaoslab.utils.glide
import com.afaryn.kaoslab.utils.hideBottomNav
import com.afaryn.kaoslab.utils.orZero
import com.afaryn.kaoslab.utils.toIdrFormat
import com.afaryn.kaoslab.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerCentreFragment : Fragment() {

    private var _binding: FragmentSellerCentreBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<SellerCentreViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNav()
        _binding = FragmentSellerCentreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
        setupToolbar()
        setupActions()
    }

    private fun getData() = binding.run {
        lifecycleScope.launch {
            vm.user.collect {
                when (it) {
                    is Response.Error -> toast(it.message)
                    is Response.Success -> {
                        txtName.text = it.data.name

                        it.data.profilePicture.takeIf { p -> p.isNotEmpty() }?.let {  p ->
                            imgProfile.glide(p)
                        }
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            vm.sales.collect {
                when (it) {
                    is Resource.Error -> toast(it.error)
                    is Resource.Success -> {
                        val sales = it.data?.size
                        toShipCount.text = sales.toString()

                        val balance = it.data?.mapNotNull { o -> o.design.selectedLicense }?.sumOf { l -> l.price }.toIdrFormat()
                        tvBalance.text = balance
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            vm.designs.collect {
                when (it) {
                    is Response.Error -> toast(it.message)
                    is Response.Success -> {
                        canceledCount.text = it.data.size.orZero().toString()
                    }
                    else -> {}
                }
            }
        }
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

        binding.btnSeeDetailSales.setOnClickListener {
            startActivity(Intent(requireContext(), DesignSalesActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}