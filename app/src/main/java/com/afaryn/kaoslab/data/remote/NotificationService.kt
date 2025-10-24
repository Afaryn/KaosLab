package com.afaryn.kaoslab.data.remote

import com.afaryn.kaoslab.domain.model.NotificationMessage
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationService {

    @POST("v1/projects/{projectId}/messages:send")
    suspend fun sendNotification(
        @Path("projectId") projectId: String = "kaaoslab",
        @Header("Content-Type") type: String = "application/json",
        @Header("Authorization") accessToken: String,
        @Body notificationMessage: NotificationMessage
    ): Response<Void>
}