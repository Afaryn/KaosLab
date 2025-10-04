package com.afaryn.kaoslab.presentation.ui_customer.desain.checkout

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DesignCheckOutViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    suspend fun getSnapToken(design: Design) = userRepository.getSnapToken(design = design)
    fun addDesign(design: Design, isPending: Boolean) = userRepository.addDesign(design, isPending)
}