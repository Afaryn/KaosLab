package com.afaryn.kaoslab.domain.model

data class NotificationMessage(
    val message: Message
)

data class Message(
    val topic: String = "kl-notification",
    val token: String? = null,
    val data: NotificationData
)

data class NotificationData(
    val userId: String? = null,
    val title: String? = null,
    val message: String? = null
)