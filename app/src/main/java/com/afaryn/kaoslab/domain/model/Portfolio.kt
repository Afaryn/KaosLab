package com.afaryn.kaoslab.domain.model

import com.google.firebase.Timestamp

data class Portfolio(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val designerId: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
