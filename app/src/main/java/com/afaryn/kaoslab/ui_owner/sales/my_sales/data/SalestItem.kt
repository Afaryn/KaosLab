package com.afaryn.kaoslab.ui_owner.sales.my_sales.data

data class SalesItem(
    val id: String,
    val userName: String,
    val userAvatarUrl: String?,
    val status: String,
    val productName: String,
    val productImageUrl: String?,
    val productDetails: String,
    val quantity: Int,
    val totalAmount: String
)