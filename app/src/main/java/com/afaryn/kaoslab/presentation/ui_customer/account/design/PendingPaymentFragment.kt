package com.afaryn.kaoslab.presentation.ui_customer.account.design

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afaryn.kaoslab.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingPaymentFragment : MyDesignBaseFragment() {
    override val isPending: Boolean = true
}