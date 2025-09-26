package com.afaryn.kaoslab.presentation.ui_designer.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.repository.AuthRepository
import com.afaryn.kaoslab.domain.repository.DesignerRepository
import com.afaryn.kaoslab.domain.model.User
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDesignerViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: DesignerRepository
) : ViewModel() {

    private val _userProfileState = MutableStateFlow<Response<User>>(Response.Idle)
    val userProfileState: StateFlow<Response<User>> = _userProfileState.asStateFlow()

    init {
        getUserProfile()
    }

    fun getUserProfile() {
        viewModelScope.launch {
            repository.getCurrentUser().collect { response ->
                _userProfileState.value = response
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}