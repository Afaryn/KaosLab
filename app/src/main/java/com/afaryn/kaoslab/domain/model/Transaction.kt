package com.afaryn.kaoslab.domain.model

import com.google.firebase.Timestamp

data class Transaction(
    val id: String = "",
    val type: TransactionType = TransactionType.PAYMENT,
    val amount: Double = 0.0,
    val customerName: String = "",
    val customerId: String = "",
    val orderId: String? = null,
    val description: String = "",
    val createdAt: Timestamp? = null
)

enum class TransactionType {
    PAYMENT,
    WITHDRAWAL
}

data class TransactionFilter(
    val type: TransactionType? = null,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null
)
