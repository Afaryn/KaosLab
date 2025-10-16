package com.afaryn.kaoslab.presentation.ui_designer.seller_centre.sales

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingPaymentFragment : DesignSalesBaseFragment() {
    override val isPending: Boolean = true
}