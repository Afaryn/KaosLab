package com.afaryn.kaoslab.presentation.ui_designer.seller_centre.sales

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.kaoslab.data.adapter.DesignSalesPagerAdapter
import com.afaryn.kaoslab.databinding.ActivityDesignSalesBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DesignSalesActivity : AppCompatActivity() {

    private var _binding: ActivityDesignSalesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDesignSalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
        setupTabLayout()
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }
    }

    private fun setupTabLayout() = binding.run {
        viewPager2.isUserInputEnabled = false

        val viewPagerAdapter = DesignSalesPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "Pending Payments"
                1 -> tab.text = "Purchased Designs"
            }
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}