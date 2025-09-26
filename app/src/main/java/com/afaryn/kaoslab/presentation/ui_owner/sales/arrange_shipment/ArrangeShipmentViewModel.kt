package com.afaryn.kaoslab.presentation.ui_owner.sales.arrange_shipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.repository.OwnerRepository
import com.afaryn.kaoslab.domain.model.Kurir
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArrangeShipmentViewModel @Inject constructor(
    private val repository: OwnerRepository
) : ViewModel() {

    private val _selectedCourier = MutableStateFlow<Kurir?>(null)
    val selectedCourier: StateFlow<Kurir?> = _selectedCourier.asStateFlow()

    private val _noResi = MutableStateFlow("")
    val noResi: StateFlow<String> = _noResi.asStateFlow()

    private val _updateOrderState = MutableStateFlow<Response<String>>(Response.Success(""))
    val updateOrderState: StateFlow<Response<String>> = _updateOrderState.asStateFlow()

    fun getCouriers() = repository.getMasterKurir().asLiveData()

    fun getOrderById(orderId: String) = repository.getOrderById(orderId).asLiveData()

    fun selectCourier(courier: Kurir) {
        _selectedCourier.value = courier
    }

    fun setNoResi(resi: String) {
        _noResi.value = resi
    }

    fun updateOrderToShipped(orderId: String) {
        viewModelScope.launch {
            val courier = _selectedCourier.value
            val resi = _noResi.value

            if (courier != null && resi.isNotEmpty()) {
                repository.updateOrderStatus(
                    orderId = orderId,
                    status = "shipped",
                    courierId = courier.id,
                    noResi = resi
                ).collect { response ->
                    _updateOrderState.value = response
                }
            } else {
                _updateOrderState.value = Response.Error("Please select courier and enter tracking number")
            }
        }
    }

    fun updateOrderToShippedWithCourierInfo(orderId: String, courierId: String, noResi: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(
                orderId = orderId,
                status = "shipped",
                courierId = courierId,
                noResi = noResi
            ).collect { response ->
                _updateOrderState.value = response
            }
        }
    }

    fun resetUpdateState() {
        _updateOrderState.value = Response.Success("")
    }
}
