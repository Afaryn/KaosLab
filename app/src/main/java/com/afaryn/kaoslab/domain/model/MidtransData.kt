package com.afaryn.kaoslab.domain.model

import com.google.gson.annotations.SerializedName

data class SnapRequest(
    val orderId: String,
    val amount: Long,
    val name: String = "",
    val email: String = ""
)

data class SnapResponse(
    val token: String
)

data class StatusResponse(
    @SerializedName("status_code")
    val statusCode: String,
    @SerializedName("transaction_status")
    val transactionStatus: String,
    @SerializedName("fraud_status")
    val fraudStatus: String,
    @SerializedName("gross_amount")
    val grossAmount: String
)