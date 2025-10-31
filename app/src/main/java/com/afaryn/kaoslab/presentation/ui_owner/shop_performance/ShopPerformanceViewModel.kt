package com.afaryn.kaoslab.presentation.ui_owner.shop_performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.repository.OwnerRepository
import com.afaryn.kaoslab.domain.model.MonthlySales
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopPerformanceViewModel @Inject constructor(
    private val ownerRepository: OwnerRepository
) : ViewModel() {

    private val _monthlySales = MutableStateFlow<Response<List<MonthlySales>>>(Response.Idle)
    val monthlySales: StateFlow<Response<List<MonthlySales>>> = _monthlySales.asStateFlow()

    init {
        loadMonthlySales()
    }

    fun loadMonthlySales() {
        viewModelScope.launch {
            ownerRepository.getMonthlySales().collect { response ->
                _monthlySales.value = response
            }
        }
    }
}
