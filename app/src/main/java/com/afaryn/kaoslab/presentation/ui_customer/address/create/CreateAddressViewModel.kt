package com.afaryn.kaoslab.presentation.ui_customer.address.create

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.model.Address
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateAddressViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun createAddress(address: Address) =
        userRepository.createAddress(address)
}