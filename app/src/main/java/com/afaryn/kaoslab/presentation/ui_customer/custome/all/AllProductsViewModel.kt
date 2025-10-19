package com.afaryn.kaoslab.presentation.ui_customer.custome.all

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllProductsViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    val allProducts = userRepository.getAllProducts()
}