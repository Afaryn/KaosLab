package com.afaryn.kaoslab.domain.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Portfolio(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val userId: String? = null,
    val user: User? = null,
    val imageUrl: String = "",
    val designerId: String = "",
    val likes: List<Likes> = emptyList(),
    val comments: List<Comments> = emptyList(),
    val createdAt: Timestamp = Timestamp.now()
): Parcelable {
    fun isLikedBy(currentUserId: String): Boolean = likes.any { it.userId == currentUserId }
}