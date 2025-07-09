package com.afaryn.kaoslab.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Product(
    val id: String = UUID.randomUUID().toString().replace("-", "").substring(0, 20),
    val name: String? = null,
    val imageUrl: String = "",
    val desc: String? = null,
    val createdBy: String = "",
//    val reviews: List<Review>? = null,
    val createdAt: Date = Date()
) : Parcelable
