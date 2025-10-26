package com.afaryn.kaoslab.domain.repository

import com.afaryn.kaoslab.domain.model.ProductTemplate
import com.afaryn.kaoslab.domain.model.BusinessInsights
import com.afaryn.kaoslab.domain.model.ChartData
import com.afaryn.kaoslab.domain.model.Kurir
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.domain.model.OrderStatusCounts
import com.afaryn.kaoslab.domain.model.Transaction
import com.afaryn.kaoslab.domain.model.TransactionFilter
import com.afaryn.kaoslab.domain.model.User
import com.afaryn.kaoslab.domain.model.MonthlySales
import com.afaryn.kaoslab.utils.Response
import kotlinx.coroutines.flow.Flow

interface OwnerRepository {
    fun getCurrentUser(): Flow<Response<User>>
    fun getProductTemplates(): Flow<List<ProductTemplate>>
    fun getMasterKurir(): Flow<Response<List<Kurir>>>
    fun addProductTemplate(productTemplate: ProductTemplate): Flow<Response<String>>
    suspend fun updateProductTemplate(productTemplate: ProductTemplate): Result<Unit>
    suspend fun deleteProductTemplate(templateId: String): Result<Unit>

    // Customer methods
    fun getCustomers(searchQuery: String = ""): Flow<Response<List<User>>>

    // Business Report methods
    fun getBusinessInsights(): Flow<Response<BusinessInsights>>
    fun getSellingProductData(period: String = "week"): Flow<Response<List<ChartData>>>

    // Home Owner methods
    fun getSalesRevenue(): Flow<Response<Double>>
    fun getOrderStatusCounts(): Flow<Response<OrderStatusCounts>>
    fun getLastOrders(limit: Int = 5): Flow<Response<List<Order>>>
    fun getOrdersByStatus(status: String): Flow<Response<List<Order>>>

    // Arrange Shipment methods
    fun updateOrderStatus(orderId: String, status: String, courierId: String? = null, noResi: String? = null): Flow<Response<String>>
    fun getOrderById(orderId: String): Flow<Response<Order>>
    fun getUserById(userId: String): Flow<Response<User>>

    // Transaction methods
    fun getTransactionHistory(filter: TransactionFilter? = null): Flow<Response<List<Transaction>>>
    fun getTotalBalance(): Flow<Response<Double>>

    // Shop Performance methods
    fun getMonthlySales(): Flow<Response<List<MonthlySales>>>
    fun getOrdersByMonth(month: String, year: Int): Flow<Response<List<Order>>>
}
