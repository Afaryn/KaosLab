package com.afaryn.kaoslab.ui_owner.sales.my_sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afaryn.kaoslab.databinding.FragmentMySalesBinding
import com.google.android.material.tabs.TabLayoutMediator

class MySalesFragment : Fragment() {
    private var _binding: FragmentMySalesBinding? = null
    private val binding get() = _binding!!

    private val tabTitles = arrayOf("Unpaid", "To Deliver", "Shipping", "Completed")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMySalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupViewPagerWithTabs()
    }

    private fun setupToolbar() {
        binding.backArrow.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun setupViewPagerWithTabs() {
        val salesPagerAdapter = SalesPagerAdapter(this)
        binding.viewPager.adapter = salesPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}