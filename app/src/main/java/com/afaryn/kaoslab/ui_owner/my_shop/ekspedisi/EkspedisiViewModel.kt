package com.afaryn.kaoslab.ui_owner.my_shop.ekspedisi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.kaoslab.data.OwnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
