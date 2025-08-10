package com.afaryn.kaoslab.model

import com.google.firebase.Timestamp

data class ProductTemplate(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val basePrice: Int = 0,
    val maxPrice: Int = 0,
    val type: String = "",
    val sizes: List<SizeOption> = emptyList(),
    val colors: List<String> = emptyList(),
    val createdAt: Timestamp? = null
)

enum class ProductType(val value: String, val displayName: String) {
    TOP("0", "Top"),
    BOTTOM("1", "Bottom"),
    HAT("2", "Hat")
}
