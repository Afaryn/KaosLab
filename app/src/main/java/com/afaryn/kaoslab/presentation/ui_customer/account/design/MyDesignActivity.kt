package com.afaryn.kaoslab.presentation.ui_customer.account.design

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afaryn.kaoslab.data.adapter.MyDesignPagerAdapter
import com.afaryn.kaoslab.databinding.ActivityMyDesignBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyDesignActivity : AppCompatActivity() {

    private var _binding: ActivityMyDesignBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMyDesignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
        setupTabLayout()
    }

    private fun setActions() = binding.run {
        btnBack.setOnClickListener { finish() }
    }

    private fun setupTabLayout() = binding.run {
        viewPager2.isUserInputEnabled = false

        val viewPagerAdapter = MyDesignPagerAdapter(supportFragmentManager, lifecycle)
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