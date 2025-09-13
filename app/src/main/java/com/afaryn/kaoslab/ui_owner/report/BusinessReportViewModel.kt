package com.afaryn.kaoslab.ui_owner.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.data.OwnerRepository
import com.afaryn.kaoslab.model.BusinessInsights
import com.afaryn.kaoslab.model.ChartData
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessReportViewModel @Inject constructor(
    private val ownerRepository: OwnerRepository
) : ViewModel() {

    private val _businessInsights = MutableStateFlow<Response<BusinessInsights>>(Response.Idle)
    val businessInsights: StateFlow<Response<BusinessInsights>> = _businessInsights.asStateFlow()

    private val _sellingProductData = MutableStateFlow<Response<List<ChartData>>>(Response.Idle)
    val sellingProductData: StateFlow<Response<List<ChartData>>> = _sellingProductData.asStateFlow()

    private val _selectedPeriod = MutableStateFlow("week")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    init {
        loadBusinessInsights()
        loadSellingProductData()
    }

    fun loadBusinessInsights() {
        viewModelScope.launch {
            ownerRepository.getBusinessInsights().collect { response ->
                _businessInsights.value = response
            }
        }
    }

    fun loadSellingProductData(period: String = "week") {
        _selectedPeriod.value = period
        viewModelScope.launch {
            ownerRepository.getSellingProductData(period).collect { response ->
                _sellingProductData.value = response
            }
        }
    }

    fun refreshData() {
        loadBusinessInsights()
        loadSellingProductData(_selectedPeriod.value)
    }
}
