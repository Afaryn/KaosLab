package com.afaryn.kaoslab.presentation.ui_customer.account.orders.fragments

import com.afaryn.kaoslab.domain.model.OrderStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingOrderFragment : OrderBaseFragment() {
    override val status: OrderStatus = OrderStatus.Pending
}