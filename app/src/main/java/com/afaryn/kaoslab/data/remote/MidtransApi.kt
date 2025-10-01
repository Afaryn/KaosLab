package com.afaryn.kaoslab.data.remote

import com.afaryn.kaoslab.domain.model.SnapRequest
import com.afaryn.kaoslab.domain.model.SnapResponse
import com.afaryn.kaoslab.domain.model.StatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MidtransApi {
    @POST("create-snap")
    suspend fun createSnap(@Body request: SnapRequest): SnapResponse

    @GET("get-status")
    suspend fun getStatus(@Query("order_id") orderId: String): StatusResponse
}