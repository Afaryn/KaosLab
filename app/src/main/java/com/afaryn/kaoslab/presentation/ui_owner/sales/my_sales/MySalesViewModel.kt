package com.afaryn.kaoslab.presentation.ui_owner.sales.my_sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.repository.OwnerRepository
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySalesViewModel @Inject constructor(
    private val ownerRepository: OwnerRepository
) : ViewModel() {

    private val _unpaidOrders = MutableStateFlow<Response<List<Order>>>(Response.Idle)
    val unpaidOrders: StateFlow<Response<List<Order>>> = _unpaidOrders.asStateFlow()

    private val _toDeliverOrders = MutableStateFlow<Response<List<Order>>>(Response.Idle)
    val toDeliverOrders: StateFlow<Response<List<Order>>> = _toDeliverOrders.asStateFlow()

    private val _shippingOrders = MutableStateFlow<Response<List<Order>>>(Response.Idle)
    val shippingOrders: StateFlow<Response<List<Order>>> = _shippingOrders.asStateFlow()

    private val _completedOrders = MutableStateFlow<Response<List<Order>>>(Response.Idle)
    val completedOrders: StateFlow<Response<List<Order>>> = _completedOrders.asStateFlow()

    fun loadOrdersByStatus(status: String) {
        viewModelScope.launch {
            // Map tab status to Firebase collection status
            val firebaseStatus = when (status) {
                "unpaid" -> "pending"
                "to_deliver" -> "processing"
                "shipped" -> "shipped"
                "completed" -> "delivered"
                else -> status
            }

            ownerRepository.getOrdersByStatus(firebaseStatus).collect { response ->
                when (status) {
                    "unpaid" -> _unpaidOrders.value = response
                    "to_deliver" -> _toDeliverOrders.value = response
                    "shipped" -> _shippingOrders.value = response
                    "completed" -> _completedOrders.value = response
                }
            }
        }
    }

    fun getUserById(userId: String) = ownerRepository.getUserById(userId).asLiveData()

    fun loadAllOrders() {
        loadOrdersByStatus("unpaid")
        loadOrdersByStatus("to_deliver")
        loadOrdersByStatus("shipped")
        loadOrdersByStatus("completed")
    }

    fun refreshOrders() {
        loadAllOrders()
    }
}
