package com.afaryn.kaoslab.data

import com.afaryn.kaoslab.model.ProductTemplate
import com.afaryn.kaoslab.model.BusinessInsights
import com.afaryn.kaoslab.model.ChartData
import com.afaryn.kaoslab.model.Order
import com.afaryn.kaoslab.model.OrderStatusCounts
import com.afaryn.kaoslab.model.Transaction
import com.afaryn.kaoslab.model.TransactionFilter
import kotlinx.coroutines.flow.Flow

interface OwnerRepository {
    fun getProductTemplates(): Flow<List<ProductTemplate>>
    fun addProductTemplate(productTemplate: ProductTemplate): Flow<com.afaryn.kaoslab.utils.Response<String>>
    suspend fun updateProductTemplate(productTemplate: ProductTemplate): Result<Unit>
    suspend fun deleteProductTemplate(templateId: String): Result<Unit>

    // Business Report methods
    fun getBusinessInsights(): Flow<com.afaryn.kaoslab.utils.Response<BusinessInsights>>
    fun getSellingProductData(period: String = "week"): Flow<com.afaryn.kaoslab.utils.Response<List<ChartData>>>

    // Home Owner methods
    fun getSalesRevenue(): Flow<com.afaryn.kaoslab.utils.Response<Double>>
    fun getOrderStatusCounts(): Flow<com.afaryn.kaoslab.utils.Response<OrderStatusCounts>>
    fun getLastOrders(limit: Int = 5): Flow<com.afaryn.kaoslab.utils.Response<List<Order>>>
    fun getOrdersByStatus(status: String): Flow<com.afaryn.kaoslab.utils.Response<List<Order>>>

    // Transaction methods
    fun getTransactionHistory(filter: TransactionFilter? = null): Flow<com.afaryn.kaoslab.utils.Response<List<Transaction>>>
    fun getTotalBalance(): Flow<com.afaryn.kaoslab.utils.Response<Double>>
}
