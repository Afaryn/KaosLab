

package com.afaryn.kaoslab.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.ui_customer.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("MyFirebaseMessagingService", "FCM Message received: ${remoteMessage.data}")

        remoteMessage.notification?.let {
            val title = it.title ?: "Pemberitahuan"
            val body = it.body ?: "Anda memiliki pesan baru."
            sendNotification(title, body)
            saveNotification(title,body)
        }
    }

    private fun sendNotification(title: String, body: String) {
        val channelId = "reservation_notifications"

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_fill) // Pastikan R.drawable.ic_notif ada di drawable
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w("MyFirebaseMessagingService", "Permission POST_NOTIFICATIONS not granted")
                return
            }
        }

        val notificationId = System.currentTimeMillis().toInt()
        NotificationManagerCompat.from(this).notify(notificationId, notification)
        Log.d("MyFirebaseMessagingService", "Notification displayed with ID: $notificationId")
    }

    private fun saveNotification(title: String, body: String) {
        val sharedPreferences = getSharedPreferences("notifications", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val timestampMillis = System.currentTimeMillis()
        val timestamp = getRelativeTimeSpanString(timestampMillis)

        val notification = "$title;$body;$timestamp;false"
        editor.putString("notification_$timestampMillis", notification)
        val isSaved = editor.commit()

        if (isSaved) {
            Log.d("MyFirebaseMessagingService", "Notification saved: $notification")
        } else {
            Log.e("MyFirebaseMessagingService", "Failed to save notification")
        }
    }

    private fun getRelativeTimeSpanString(timestampMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestampMillis

        return when {
            diff < 60_000 -> "just now"
            diff < 3_600_000 -> "${diff / 60_000} minutes ago"
            diff < 86_400_000 -> "${diff / 3_600_000} hours ago"
            else -> "${diff / 86_400_000} days ago"
        }
    }

}
