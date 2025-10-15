package com.afaryn.kaoslab.presentation.ui_customer.account.orders

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.kaoslab.data.adapter.OrdersPagerAdapter
import com.afaryn.kaoslab.databinding.ActivityOrdersBinding
import com.afaryn.kaoslab.domain.model.OrderStatus
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersActivity : AppCompatActivity() {

    private var _binding: ActivityOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
        setupTabLayout()
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }
    }

    private fun setupTabLayout() = binding.run {
        viewPager2.isUserInputEnabled = false

        val viewPagerAdapter = OrdersPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            val status = when (position) {
                0 -> OrderStatus.Pending.value
                1 -> OrderStatus.Processing.value
                2 -> OrderStatus.Shipped.value
                3 -> OrderStatus.Delivered.value
                else -> ""
            }

            tab.text = status.replaceFirstChar { it.uppercaseChar() }
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}