package com.afaryn.kaoslab.domain.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Order(
    val orderId: String = UUID.randomUUID().toString().replace("-", "").substring(0, 20),
    val customerId: String = "",
    val designId: String = "",
    val status: String = OrderStatus.Pending.value,
    val totalAmount: Double = 0.0,
    val totalPieces: Int = 0,
    val cartProducts: List<CartProduct> = emptyList(),
    val courierId: String? = null,
    val noResi: String? = null,
    val snapToken: String? = null,
    val createdAt: Timestamp? = Timestamp(Date()),
    @Transient val customerName: String = "",
    @Transient val customerAvatarUrl: String = "",
    @Transient val courierInfo: String? = null,
    @Transient val courierLogo: String? = null,
    @Transient val designImageUrl: String = "",
    @Transient val title: String = ""
): Parcelable

sealed class DesignUplType(val value: String) {
    object Upload : DesignUplType("image_upload")
    object URL : DesignUplType("image_your_design")
    object Text : DesignUplType("text")
}

sealed class OrderStatus(val value: String) {
    object Pending : OrderStatus("pending")
    object Processing : OrderStatus("processing")
    object Shipped : OrderStatus("shipped")
    object Delivered : OrderStatus("delivered")

    companion object {
        fun from(value: String?): OrderStatus = when (value) {
            "processing" -> Processing
            "shipped" -> Shipped
            "delivered" -> Delivered
            else -> Pending
        }
    }
}

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
