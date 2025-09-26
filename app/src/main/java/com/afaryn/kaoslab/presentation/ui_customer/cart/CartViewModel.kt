package com.afaryn.kaoslab.presentation.ui_customer.cart

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.model.CartProduct
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun getCart() = userRepository.getCartProducts()

    fun deleteCart(id: String) = userRepository.deleteCartProduct(id)

    fun calculatePrice(data: List<CartProduct>): Double {
        return data.sumOf {
            it.totalAmount
        }
    }

    fun totalQty(data: List<CartProduct>): Int {
        return data.sumOf {
            it.quantity
        }
    }
}