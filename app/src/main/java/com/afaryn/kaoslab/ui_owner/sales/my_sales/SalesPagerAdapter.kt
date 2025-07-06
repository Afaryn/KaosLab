package com.afaryn.kaoslab.ui_owner.sales.my_sales

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SalesPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabTitles = arrayOf("Unpaid", "To Deliver", "Shipping", "Completed")

    override fun getItemCount(): Int = tabTitles.size

    override fun createFragment(position: Int): Fragment {
        val status = tabTitles[position]
        return SalesListFragment.newInstance(status)
    }
}