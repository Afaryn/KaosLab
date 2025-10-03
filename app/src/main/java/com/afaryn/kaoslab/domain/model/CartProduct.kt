package com.afaryn.kaoslab.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class CartProduct(
    val id: String = UUID.randomUUID().toString(),
    val orderItem: OrderItem? = null,
    val quantity: Int = 0,
    val totalAmount: Double = 0.0,
    val type: String = ItemType.Custom.value,
    val selectedColor: String? = null
): Parcelable

sealed class ItemType(val value: String) {
    object Design : DesignUplType("design")
    object Custom : DesignUplType("custom")
}