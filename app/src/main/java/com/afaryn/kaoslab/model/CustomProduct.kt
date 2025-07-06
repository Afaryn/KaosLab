package com.afaryn.kaoslab.model

import java.util.Date
import java.util.UUID

data class CustomProduct(
    val id: String = UUID.randomUUID().toString().replace("-", "").substring(0, 20),
    val type:String = "",
    val name: String = "",
    val imageUrl: String = "",
    val basePrice: Int = 0,
    val maxPrice: Int = 0,
    val sizes: List<SizeOption> = emptyList(),
    val colors: List<String> = emptyList(),
    val createdAt: com.google.firebase.Timestamp? = null
)

data class SizeOption(
    val label: String = "",
    val additionalPrice: Int = 0
)
