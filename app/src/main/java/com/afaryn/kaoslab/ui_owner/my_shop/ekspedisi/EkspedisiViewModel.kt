package com.afaryn.kaoslab.ui_owner.my_shop.ekspedisi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.data.OwnerRepository
import com.afaryn.kaoslab.model.Order
import com.afaryn.kaoslab.model.OrderStatusCounts
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EkspedisiViewModel @Inject constructor(
    private val ownerRepository: OwnerRepository
) : ViewModel() {

    init {
        getListEkspedisi()
    }

    fun getListEkspedisi() = ownerRepository.getMasterKurir().asLiveData()
}
