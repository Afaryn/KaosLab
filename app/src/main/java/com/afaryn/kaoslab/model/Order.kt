package com.afaryn.kaoslab.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val orderId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerAvatarUrl: String = "",
    val designId: String = "",
    val status: String = "",
    val totalAmount: Double = 0.0,
    val totalPieces: Int = 0,
    val size: String = "",
    val title: String = "",
    val designImageUrl: String = "",
    val courierInfo: String? = null,
    val courierLogo: String? = null,
    val trackingNumber: String? = null,
    val createdAt: Timestamp? = null
):Parcelable

data class BusinessInsights(
    val totalOrders: Int = 0,
    val totalSales: Double = 0.0,
    val totalVisitors: Int = 0,
    val totalBuyers: Int = 0,
    val totalStock: Int = 0
)

data class ChartData(
    val label: String = "",
    val value: Float = 0f
)
