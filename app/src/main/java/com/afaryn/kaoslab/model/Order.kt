package com.afaryn.kaoslab.model

import com.google.firebase.Timestamp

data class Order(
    val orderId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerAvatarUrl: String = "",
    val designId: String = "",
    val status: String = "", // "unpaid", "to_deliver", "shipping", "completed"
    val totalAmount: Double = 0.0,
    val totalPieces: Int = 0,
    val size: String = "",
    val title: String = "",
    val designImageUrl: String = "",
    val courierInfo: String? = null, // For "to_deliver" and "shipping" status
    val courierLogo: String? = null, // URL for courier logo
    val trackingNumber: String? = null, // For "shipping" status
    val createdAt: Timestamp? = null
)

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
