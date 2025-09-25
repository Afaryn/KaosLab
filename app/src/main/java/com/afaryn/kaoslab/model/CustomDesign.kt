package com.afaryn.kaoslab.model

import com.google.firebase.Timestamp

data class CustomDesign(
    val designId: String = "",
    val customerId: String = "",
    val baseTemplateId: String = "", // nullable - references customproduct collection
    val imageUrl: String = "",
    val status: String = "", // "draft" | "completed"
    val customizedFields: Map<String, Any> = emptyMap(),
    val createdAt: Timestamp? = null
)

data class OrderItem(
    val orderId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerAvatarUrl: String = "",
    val status: String = "", // "unpaid" | "to_deliver" | "shipping" | "completed"
    val courierName: String = "", // For "to_deliver" and "shipping" status
    val courierLogo: String = "", // URL for courier logo
    val trackingNumber: String = "", // For "shipping" status
    val totalAmount: Double = 0.0,
    val totalPieces: Int = 0,
    val size: String = "",
    val title: String = "",
    val designImageUrl: String = "",
    val createdAt: Timestamp? = null,
    val customDesign: CustomDesign? = null
)

data class OrderStatusCounts(
    val unpaid: Int = 0,
    val toShip: Int = 0,
    val shipped: Int = 0,
    val success: Int = 0
)
