package com.afaryn.kaoslab.presentation.ui_customer.desain.checkout

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.model.DesignOrder
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DesignCheckOutViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    suspend fun getSnapToken(design: DesignOrder) = userRepository.getSnapToken(design = design)
    fun addDesign(design: DesignOrder) = userRepository.addDesign(design)
}