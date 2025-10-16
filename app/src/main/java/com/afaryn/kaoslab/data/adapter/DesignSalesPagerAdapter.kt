package com.afaryn.kaoslab.data.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.afaryn.kaoslab.presentation.ui_designer.seller_centre.sales.PendingPaymentFragment
import com.afaryn.kaoslab.presentation.ui_designer.seller_centre.sales.PurchasedDesignsFragment

class DesignSalesPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingPaymentFragment()
            1 -> PurchasedDesignsFragment()
            else -> throw IndexOutOfBoundsException()
        }
    }
}