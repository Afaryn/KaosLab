package com.afaryn.kaoslab.presentation.ui_customer.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
): ViewModel() {

    val notifications = notificationRepository.get()

    fun deleteNotification(id: Int) = viewModelScope.launch {
        notificationRepository.delete(id)
    }

    fun clearNotification() = viewModelScope.launch {
        notificationRepository.deleteAll()
    }
}