package com.afaryn.kaoslab.data.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.afaryn.kaoslab.presentation.ui_customer.account.orders.fragments.DeliveredOrderFragment
import com.afaryn.kaoslab.presentation.ui_customer.account.orders.fragments.PendingOrderFragment
import com.afaryn.kaoslab.presentation.ui_customer.account.orders.fragments.ProcessingOrderFragment
import com.afaryn.kaoslab.presentation.ui_customer.account.orders.fragments.ShippedOrderFragment

class OrdersPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingOrderFragment()
            1 -> ProcessingOrderFragment()
            2 -> ShippedOrderFragment()
            3 -> DeliveredOrderFragment()
            else -> throw IndexOutOfBoundsException()
        }
    }
}