package com.afaryn.kaoslab.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Design(
    val id: String = "",
    val designerId: String = "",
    val designerName: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val fileUrl: String = "",
    val thumbnailUrl: String = "",
    val licenses: List<License> = listOf(),
    val tags: List<String> = listOf(),
    val minPrice: Double = 0.0, // Lowest price from all licenses
    val maxPrice: Double = 0.0, // Highest price from all licenses
    val downloads: Int = 0,
    val rating: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
): Parcelable

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
