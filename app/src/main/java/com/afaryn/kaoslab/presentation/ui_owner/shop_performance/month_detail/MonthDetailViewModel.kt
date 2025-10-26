package com.afaryn.kaoslab.presentation.ui_owner.shop_performance.month_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.domain.repository.OwnerRepository
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthDetailViewModel @Inject constructor(
    private val repository: OwnerRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<Response<List<Order>>>(Response.Idle)
    val orders: StateFlow<Response<List<Order>>> = _orders.asStateFlow()

    fun loadOrdersByMonth(month: String, year: Int) {
        viewModelScope.launch {
            repository.getOrdersByMonth(month, year).collect { response ->
                _orders.value = response
            }
        }
    }
}

