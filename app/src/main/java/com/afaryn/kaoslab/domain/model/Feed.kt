package com.afaryn.kaoslab.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Feed(
    val id: String = UUID.randomUUID().toString(),
    val userId: String? = null,
    val user: User? = null,
    val caption: String? = null,
    val photoUrl: String? = null,
    val likes: List<Likes> = emptyList(),
    val comments: List<Comments> = emptyList(),
    val createdAt: Date = Date()
): Parcelable {
    fun isLikedBy(currentUserId: String): Boolean = likes.any { it.userId == currentUserId }
}

@Parcelize
data class Comments(
    val user: User? = null,
    val comment: String? = null,
    val createdAt: Date = Date()
): Parcelable

@Parcelize
data class Likes(
    val userId: String? = null,
    val createdAt: Date = Date()
): Parcelable