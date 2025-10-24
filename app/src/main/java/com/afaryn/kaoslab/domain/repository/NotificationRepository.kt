package com.afaryn.kaoslab.domain.repository

import com.afaryn.kaoslab.data.local.room.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    suspend fun delete(notificationId: Int)
    suspend fun deleteAll()
    fun get(): Flow<List<NotificationEntity>>
    fun publishPaymentSuccess(targetId: String? = null, productName: String)
    fun publishOrderShipped(targetId: String, productName: String)
    fun publishOrderDelivered(targetId: String, productName: String)
}