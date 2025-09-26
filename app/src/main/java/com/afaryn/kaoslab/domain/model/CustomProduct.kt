package com.afaryn.kaoslab.domain.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class CustomProduct(
    val id: String = UUID.randomUUID().toString().replace("-", "").substring(0, 20),
    val type:String = "",
    val name: String = "",
    val imageUrl: String = "",
    val basePrice: Int = 0,
    val maxPrice: Int = 0,
    val sizes: List<SizeOption> = emptyList(),
    val colors: List<String> = emptyList(),
    val createdAt: Timestamp? = null
): Parcelable

@Parcelize
data class SizeOption(
    val label: String = "",
    val additionalPrice: Int = 0
): Parcelable
