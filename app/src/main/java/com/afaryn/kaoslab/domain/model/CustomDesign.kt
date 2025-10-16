package com.afaryn.kaoslab.domain.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class CustomDesign(
    val designId: String = "",
    val customerId: String = "",
    val baseTemplateId: String = "", // nullable - references customproduct collection
    val imageUrl: String = "",
    val status: String = "", // "draft" | "completed"
    val createdAt: Timestamp? = null
): Parcelable

@Parcelize
data class OrderItem(
    val orderId: String = "",
    val size: String = "",
    val title: String = "",
    val designId: String = "",
    val designType: DesignType? = null,
    @ServerTimestamp val createdAt: Date = Date(),
): Parcelable

@Parcelize
data class DesignType(
    val type: String? = null,
    val product: CustomProduct? = null,
    val overlay: String? = null,
    val text: String? = null
): Parcelable

data class OrderStatusCounts(
    val unpaid: Int = 0,
    val toShip: Int = 0,
    val shipped: Int = 0,
    val success: Int = 0
)
