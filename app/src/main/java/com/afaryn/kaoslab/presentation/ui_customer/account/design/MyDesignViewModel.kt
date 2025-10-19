package com.afaryn.kaoslab.presentation.ui_customer.account.design

import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.model.DesignOrder
import com.afaryn.kaoslab.domain.model.DesignOrderStatus
import com.afaryn.kaoslab.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyDesignViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun getDesigns(status: DesignOrderStatus) = userRepository.getOwnedDesigns(status)
    fun updateStatus(order: DesignOrder) = userRepository.updateDesignPaymentStatus(order)
    fun setDownloaded(order: DesignOrder) = userRepository.downloadDesign(order)
}