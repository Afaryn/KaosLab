package com.afaryn.kaoslab.ui_owner.sales.order_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.data.OwnerRepository
import com.afaryn.kaoslab.model.Order
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val repository: OwnerRepository
) : ViewModel() {

    private val _updateOrderState = MutableStateFlow<Response<String>>(Response.Success(""))
    val updateOrderState: StateFlow<Response<String>> = _updateOrderState.asStateFlow()

    fun getOrderById(orderId: String) = repository.getOrderById(orderId).asLiveData()

    fun getUserById(userId: String) = repository.getUserById(userId).asLiveData()

    fun setOrderToDelivered(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(
                orderId = orderId,
                status = "delivered"
            ).collect { response ->
                _updateOrderState.value = response
            }
        }
    }

    fun resetUpdateState() {
        _updateOrderState.value = Response.Success("")
    }
}
