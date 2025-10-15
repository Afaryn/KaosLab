package com.afaryn.kaoslab.presentation.ui_customer.account.orders

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun getOrders(status: String) = userRepository.getOrders(status)
    fun updateStatus(order: Order) = userRepository.updatePaymentStatus(order)
}