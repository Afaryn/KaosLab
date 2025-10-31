package com.afaryn.kaoslab.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class DesignOrder(
    val id: String = UUID.randomUUID().toString(),
    val userId: String? = null,
    val design: Design = Design(),
    val snapToken: String? = null,
    val status: String = DesignOrderStatus.Pending.value,
    val downloaded: Int = 0,
    val rated: Boolean? = false,
    val createdAt: Date = Date()
): Parcelable

@Parcelize
data class Design(
    val id: String = UUID.randomUUID().toString(),
    val designerId: String = "",
    val designerName: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val fileUrl: String = "",
    val thumbnailUrl: String = "",
    val licenses: List<License> = listOf(),
    val selectedLicense: License? = null,
    val tags: List<String> = listOf(),
    val minPrice: Double = 0.0, // Lowest price from all licenses
    val maxPrice: Double = 0.0, // Highest price from all licenses
    val downloads: Int = 0,
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val ratingTotal: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
): Parcelable {
    fun calculateRating(newRating: Float): Design {
        val newTotal = ratingTotal + newRating
        val newCount = ratingCount + 1
        val newAverage = newTotal / newCount

        return this.copy(
            ratingTotal = newTotal,
            ratingCount = newCount,
            rating = newAverage,
            updatedAt = System.currentTimeMillis()
        )
    }
}

@Parcelize
data class License(
    val id: String = "",
    val type: String = "", // "standard", "exclusive", "custom"
    val name: String = "",
    val description: String = "",
    val features: List<String> = listOf(), // List of features included
    val price: Double = 0.0,
    val isDefault: Boolean = false
): Parcelable

sealed class DesignOrderStatus(val value: String) {
    object Pending : DesignOrderStatus("pending")
    object Owned : DesignOrderStatus("owned")
}