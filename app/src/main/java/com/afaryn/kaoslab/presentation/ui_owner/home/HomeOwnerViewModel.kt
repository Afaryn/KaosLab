package com.afaryn.kaoslab.presentation.ui_owner.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.repository.OwnerRepository
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.domain.model.OrderStatusCounts
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeOwnerViewModel @Inject constructor(
    private val ownerRepository: OwnerRepository
) : ViewModel() {

    private val _salesRevenue = MutableStateFlow<Response<Double>>(Response.Idle)
    val salesRevenue: StateFlow<Response<Double>> = _salesRevenue.asStateFlow()

    private val _orderStatusCounts = MutableStateFlow<Response<OrderStatusCounts>>(Response.Idle)
    val orderStatusCounts: StateFlow<Response<OrderStatusCounts>> = _orderStatusCounts.asStateFlow()

    private val _lastOrders = MutableStateFlow<Response<List<Order>>>(Response.Idle)
    val lastOrders: StateFlow<Response<List<Order>>> = _lastOrders.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        loadSalesRevenue()
        loadOrderStatusCounts()
        loadLastOrders()
    }

    private fun loadSalesRevenue() {
        viewModelScope.launch {
            ownerRepository.getSalesRevenue().collect { response ->
                _salesRevenue.value = response
            }
        }
    }

    private fun loadOrderStatusCounts() {
        viewModelScope.launch {
            ownerRepository.getOrderStatusCounts().collect { response ->
                _orderStatusCounts.value = response
            }
        }
    }

    private fun loadLastOrders() {
        viewModelScope.launch {
            ownerRepository.getLastOrders(5).collect { response ->
                _lastOrders.value = response
            }
        }
    }

    fun refreshData() {
        loadHomeData()
    }
}
