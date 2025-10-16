package com.afaryn.kaoslab.presentation.ui_customer.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun user() = authRepository.getCurrentUser()
}