package com.afaryn.kaoslab.presentation.ui_customer.cart.checkout

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckOutViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun getLastAddress() = userRepository.getLastAddress()
}