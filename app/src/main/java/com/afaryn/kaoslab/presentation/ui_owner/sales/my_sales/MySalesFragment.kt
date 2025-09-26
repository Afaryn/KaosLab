package com.afaryn.kaoslab.presentation.ui_owner.sales.my_sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentMySalesBinding
import com.afaryn.kaoslab.utils.hideBottomNavOwner
import com.afaryn.kaoslab.utils.showBottomNavOwner
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MySalesFragment : Fragment() {
    private var _binding: FragmentMySalesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MySalesViewModel by viewModels()
    private val tabTitles = arrayOf("Unpaid", "To Deliver", "Shipping", "Completed")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMySalesBinding.inflate(inflater, container, false)
        hideBottomNavOwner()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupViewPagerWithTabs()

        // Load all orders data
        viewModel.loadAllOrders()
    }

    private fun setupToolbar() {
        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupViewPagerWithTabs() {
        val salesPagerAdapter = SalesPagerAdapter(this)
        binding.viewPager.adapter = salesPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // Apply custom styling to tabs
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
            tab?.view?.setBackgroundResource(R.drawable.tab_background_selector)

            // Add margins for spacing between tabs
            val layoutParams = tab?.view?.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.let {
                it.setMargins(8, 8, 8, 8)
                tab.view.layoutParams = it
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}