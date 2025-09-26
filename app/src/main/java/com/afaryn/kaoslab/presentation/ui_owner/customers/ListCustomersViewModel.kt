package com.afaryn.kaoslab.presentation.ui_owner.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.repository.OwnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ListCustomersViewModel @Inject constructor(
    private val repository: OwnerRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val customers = _searchQuery
        .flatMapLatest { query ->
            repository.getCustomers(query)
        }
        .asLiveData(viewModelScope.coroutineContext)

    fun searchCustomers(query: String) {
        _searchQuery.value = query
    }

    fun refreshCustomers() {
        _searchQuery.value = _searchQuery.value
    }
}
