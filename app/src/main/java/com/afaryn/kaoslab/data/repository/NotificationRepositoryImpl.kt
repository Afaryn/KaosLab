package com.afaryn.kaoslab.data.repository

import android.content.Context
import android.util.Log
import com.afaryn.kaoslab.data.local.room.NotificationDao
import com.afaryn.kaoslab.data.remote.NotificationService
import com.afaryn.kaoslab.domain.model.Message
import com.afaryn.kaoslab.domain.model.NotificationData
import com.afaryn.kaoslab.domain.model.NotificationMessage
import com.afaryn.kaoslab.domain.repository.NotificationRepository
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.concurrent.Executors
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val notificationService: NotificationService,
    private val notificationDao: NotificationDao
): NotificationRepository {

    override suspend fun delete(notificationId: Int) =
        notificationDao.delete(notificationId)

    override suspend fun deleteAll() =
        notificationDao.deleteAll()

    override fun get() =
        notificationDao.get()

    override fun publishPaymentSuccess(targetId: String?, productName: String) {
        pushNotification(NotificationMessage(
            message = Message(
                data = NotificationData(
                    userId = auth.uid,
                    title = "Payment Confirmed",
                    message = "your payment for product $productName is confirmed!"
                )
            )
        ))
    }

    override fun publishOrderShipped(targetId: String, productName: String) {
        pushNotification(NotificationMessage(
            message = Message(
                data = NotificationData(
                    userId = targetId,
                    title = "Order shipped",
                    message = "your order for $productName has just been shipped!"
                )
            )
        ))
    }

    override fun publishOrderDelivered(targetId: String, productName: String) {
        pushNotification(NotificationMessage(
            message = Message(
                data = NotificationData(
                    userId = targetId,
                    title = "Your order is completed",
                    message = "your order for $productName has just been delivered!"
                )
            )
        ))
    }

    fun pushNotification(notificationMessage: NotificationMessage) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = getAccessToken()

                val response = notificationService.sendNotification(
                    accessToken = "Bearer $token",
                    notificationMessage = notificationMessage
                )

                if (response.isSuccessful) {
                    Log.i("NOTIFICATION", "Notification Sent")
                    Result.success(Unit)
                } else {
                    Log.i("NOTIFICATION", "Notification Failed: ${response.errorBody()?.string()}")
                    Result.failure(
                        Exception(
                            "Failed to send notification: ${response.errorBody()?.string()}"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun getAccessToken(): String? {
        return Executors.newSingleThreadExecutor().submit<String> {
            try {
                val inputStream: InputStream = context.assets.open("access-key.json")
                val credentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

                credentials.refreshIfExpired()
                val token = credentials.accessToken.tokenValue

                inputStream.close()
                token
            } catch (_: Exception) {
                null
            }
        }.get()
    }
}