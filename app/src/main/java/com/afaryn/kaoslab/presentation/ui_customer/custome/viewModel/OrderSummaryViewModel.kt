package com.afaryn.kaoslab.presentation.ui_customer.custome.viewModel

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.repository.UserRepository
import com.afaryn.kaoslab.domain.model.CartProduct
import com.afaryn.kaoslab.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class OrderSummaryViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun addToCart(cartProduct: CartProduct): Flow<Resource<Unit>> =
        userRepository.addToCart(cartProduct)
}