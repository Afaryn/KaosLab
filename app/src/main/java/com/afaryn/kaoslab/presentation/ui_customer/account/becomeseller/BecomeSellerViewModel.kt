package com.afaryn.kaoslab.presentation.ui_customer.account.becomeseller

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BecomeSellerViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun becomeSeller(accountNo: String, type: String) = userRepository.becomeSeller(accountNo, type)
}