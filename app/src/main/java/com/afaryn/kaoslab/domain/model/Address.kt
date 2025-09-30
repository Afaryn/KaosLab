package com.afaryn.kaoslab.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Address(
    val id: String = UUID.randomUUID().toString(),
    val userId: String? = null,
    val name: String? = null,
    val detail: String? = null,
    val location: String? = null,
    val postalCode: String? = null,
    val createdAt: Date? = Date()
): Parcelable
