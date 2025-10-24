package com.afaryn.kaoslab.data.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.data.local.preferences.AppPreferences
import com.afaryn.kaoslab.data.local.room.NotificationDao
import com.afaryn.kaoslab.data.local.room.entity.NotificationEntity
import com.afaryn.kaoslab.presentation.splash_screen.SplashScreenActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
@AndroidEntryPoint
class NotificationMessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var notificationDao: NotificationDao

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        serviceScope.launch {
            if (message.isEligible()) saveNotification(message)
        }
    }

    private suspend fun saveNotification(message: RemoteMessage) = with(message) {
        val (title, message) = (data["title"] ?: return@with) to (data["message"] ?: return@with)

        val notificationEntity = NotificationEntity(
            title = title, message = message, createdAt = System.currentTimeMillis()
        )

        notificationDao.insert(notificationEntity)
        showNotification(notificationEntity)
    }

    private fun showNotification(notification: NotificationEntity) {
        val intent = Intent(applicationContext, SplashScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_sablonku).setContentTitle(notification.title)
            .setContentText(notification.message).setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(soundUri, audioAttributes)
                enableVibration(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
        }

        notificationManager.notify(Random().nextInt(), builder.build())
    }

    private fun RemoteMessage.isEligible(): Boolean {
        val isEnabled = AppPreferences.isNotificationEnabled(this@NotificationMessagingService)
        val userId = data["userId"]

        return isEnabled && userId == auth.uid
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "notification-channel"
        const val CHANNEL_NAME = "channel-rumahaspirasi"
    }
}