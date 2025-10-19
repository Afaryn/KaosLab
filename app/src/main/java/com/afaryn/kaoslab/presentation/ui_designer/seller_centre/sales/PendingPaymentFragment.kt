package com.afaryn.kaoslab.presentation.ui_designer.seller_centre.sales

import com.afaryn.kaoslab.domain.model.DesignOrderStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingPaymentFragment : DesignSalesBaseFragment() {
    override val status: DesignOrderStatus = DesignOrderStatus.Pending
}