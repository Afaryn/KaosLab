package com.afaryn.kaoslab.presentation.ui_customer.address

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun getAddress() =
        userRepository.getAddress()

    fun deleteAddress(id: String) =
        userRepository.deleteAddress(id)
}