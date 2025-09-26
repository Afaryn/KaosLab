package com.afaryn.kaoslab.presentation.ui_owner.sales.my_sales

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SalesPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SalesTabFragment.newInstance("unpaid")
            1 -> SalesTabFragment.newInstance("to_deliver")
            2 -> SalesTabFragment.newInstance("shipping")
            3 -> SalesTabFragment.newInstance("completed")
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
