package com.afaryn.kaoslab.ui_owner.my_shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyShopViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun logOut() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun getCurrentUser() = authRepository.getCurrentUser().asLiveData()
}