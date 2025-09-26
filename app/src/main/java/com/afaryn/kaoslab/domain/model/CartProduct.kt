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
    val selectedColor: String? = null
): Parcelable
