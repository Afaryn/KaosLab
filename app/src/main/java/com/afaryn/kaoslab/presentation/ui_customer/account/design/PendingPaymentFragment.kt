package com.afaryn.kaoslab.presentation.ui_customer.account.design

import com.afaryn.kaoslab.domain.model.DesignOrderStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingPaymentFragment : MyDesignBaseFragment() {
    override val status: DesignOrderStatus = DesignOrderStatus.Pending
}