package com.afaryn.kaoslab.presentation.ui_owner.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.repository.OwnerRepository
import com.afaryn.kaoslab.domain.model.Transaction
import com.afaryn.kaoslab.domain.model.TransactionFilter
import com.afaryn.kaoslab.domain.model.TransactionType
import com.afaryn.kaoslab.utils.Response
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesRevenueViewModel @Inject constructor(
    private val ownerRepository: OwnerRepository
) : ViewModel() {

    private val _totalBalance = MutableStateFlow<Response<Double>>(Response.Idle)
    val totalBalance: StateFlow<Response<Double>> = _totalBalance.asStateFlow()

    private val _transactionHistory = MutableStateFlow<Response<List<Transaction>>>(Response.Idle)
    val transactionHistory: StateFlow<Response<List<Transaction>>> = _transactionHistory.asStateFlow()

    private val _selectedTransactionType = MutableStateFlow<TransactionType?>(null)
    val selectedTransactionType: StateFlow<TransactionType?> = _selectedTransactionType.asStateFlow()

    private val _selectedDateRange = MutableStateFlow<Pair<Timestamp?, Timestamp?>>(Pair(null, null))
    val selectedDateRange: StateFlow<Pair<Timestamp?, Timestamp?>> = _selectedDateRange.asStateFlow()

    init {
        getTotalBalance()
        getTransactionHistory()
    }

    fun getTotalBalance() {
        viewModelScope.launch {
            ownerRepository.getTotalBalance().collect { response ->
                _totalBalance.value = response
            }
        }
    }

    fun getTransactionHistory() {
        viewModelScope.launch {
            val filter = createTransactionFilter()
            ownerRepository.getTransactionHistory(filter).collect { response ->
                _transactionHistory.value = response
            }
        }
    }

    fun setTransactionTypeFilter(type: TransactionType?) {
        _selectedTransactionType.value = type
        getTransactionHistory()
    }

    fun setDateRangeFilter(startDate: Timestamp?, endDate: Timestamp?) {
        _selectedDateRange.value = Pair(startDate, endDate)
        getTransactionHistory()
    }

    fun clearFilters() {
        _selectedTransactionType.value = null
        _selectedDateRange.value = Pair(null, null)
        getTransactionHistory()
    }

    private fun createTransactionFilter(): TransactionFilter? {
        val type = _selectedTransactionType.value
        val dateRange = _selectedDateRange.value

        return if (type != null || (dateRange.first != null && dateRange.second != null)) {
            TransactionFilter(
                type = type,
                startDate = dateRange.first,
                endDate = dateRange.second
            )
        } else {
            null
        }
    }
}
