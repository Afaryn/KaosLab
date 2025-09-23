package com.afaryn.kaoslab.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Order(
    val orderId: String = UUID.randomUUID().toString().replace("-", "").substring(0, 20),
    val customerId: String = "",
    val designId: String = "",
    val status: String = "",
    val totalAmount: Double = 0.0,
    val totalPieces: Int = 0,
    val size: String = "",
    val courierId: String? = null,
    val noResi: String? = null,
    val createdAt: Timestamp? = null,
    // Transient fields - fetched dynamically in UI
    @Transient val customerName: String = "",
    @Transient val customerAvatarUrl: String = "",
    @Transient val courierInfo: String? = null,
    @Transient val courierLogo: String? = null,
    @Transient val designImageUrl: String = "",
    @Transient val title: String = ""
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
