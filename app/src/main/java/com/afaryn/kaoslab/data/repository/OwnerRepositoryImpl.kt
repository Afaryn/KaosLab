package com.afaryn.kaoslab.data.repository

import com.afaryn.kaoslab.domain.model.BusinessInsights
import com.afaryn.kaoslab.domain.model.ChartData
import com.afaryn.kaoslab.domain.model.Kurir
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.domain.model.OrderStatusCounts
import com.afaryn.kaoslab.domain.model.ProductTemplate
import com.afaryn.kaoslab.domain.model.SizeOption
import com.afaryn.kaoslab.domain.model.Transaction
import com.afaryn.kaoslab.domain.model.TransactionFilter
import com.afaryn.kaoslab.domain.model.TransactionType
import com.afaryn.kaoslab.domain.model.User
import com.afaryn.kaoslab.domain.repository.NotificationRepository
import com.afaryn.kaoslab.domain.repository.OwnerRepository
import com.afaryn.kaoslab.utils.Response
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OwnerRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val notificationRepository: NotificationRepository
) : OwnerRepository {
    companion object {
        private const val COLLECTION_CUSTOM_PRODUCTS = "customproduct"
        private const val COLLECTION_ORDERS = "orders"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_CUSTOM_DESIGNS = "customDesigns"
        private const val COLLECTION_EKSPEDISI = "masterEkspedisi"
    }

    override fun getCurrentUser(): Flow<Response<User>> = flow {
        try {
            emit(Response.Loading)
            val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            val snapshot = firestore.collection("users")
                .document(currentUserId)
                .get()
                .await()

            if (snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                if (user != null) {
                    emit(Response.Success(user))
                } else {
                    emit(Response.Error("User data is corrupted"))
                }
            } else {
                emit(Response.Error("User not found"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to get user profile"))
        }
    }

    override fun getProductTemplates(): Flow<List<ProductTemplate>> = callbackFlow {
        try {
            val snapshot = firestore.collection(COLLECTION_CUSTOM_PRODUCTS)
                .get()
                .await()

            val templates = snapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data ?: return@mapNotNull null

                    val sizes = (data["sizes"] as? List<Map<String, Any>>)?.map { sizeMap ->
                        SizeOption(
                            label = sizeMap["label"] as? String ?: "",
                            additionalPrice = (sizeMap["additionalPrice"] as? Long)?.toInt() ?: 0
                        )
                    } ?: emptyList()

                    val colors = (data["colors"] as? List<String>) ?: emptyList()

                    ProductTemplate(
                        id = document.id,
                        name = data["name"] as? String ?: "",
                        imageUrl = data["imageUrl"] as? String ?: "",
                        basePrice = (data["basePrice"] as? Long)?.toInt() ?: 0,
                        maxPrice = (data["maxPrice"] as? Long)?.toInt() ?: 0,
                        type = data["type"] as? String ?: "",
                        sizes = sizes,
                        colors = colors,
                        createdAt = data["createdAt"] as? Timestamp
                    )
                } catch (e: Exception) {
                    null
                }
            }
            trySend(templates)
            close()
        } catch (e: Exception) {
            close(e)
        }

        awaitClose { }
    }

    override fun getMasterKurir(): Flow<Response<List<Kurir>>> = flow {
        try {
            val snapshot = firestore.collection(COLLECTION_EKSPEDISI)
                .get()
                .await()
            if (snapshot != null) {
                val kurirData = snapshot.toObjects(Kurir::class.java)
                    ?: throw Exception("No courier data found")
                emit(Response.Success(kurirData))
            } else {
                emit(Response.Error("No courier data found"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to fetch courier data"))
        }
    }

    override fun addProductTemplate(productTemplate: ProductTemplate): Flow<Response<String>> =
        callbackFlow {
            trySend(Response.Loading)
            try {
                firestore.collection(COLLECTION_CUSTOM_PRODUCTS)
                    .document(productTemplate.id)
                    .set(productTemplate)
                    .addOnSuccessListener {
                        trySend(Response.Success(productTemplate.id))
                        close()
                    }
                    .addOnFailureListener { e ->
                        trySend(Response.Error(e.message ?: "Unknown error"))
                        close()
                    }
            } catch (e: Exception) {
                trySend(Response.Error(e.message ?: "Unknown error"))
                close()
            }
            awaitClose { }
        }

    override suspend fun updateProductTemplate(productTemplate: ProductTemplate): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_CUSTOM_PRODUCTS)
                .document(productTemplate.id)
                .set(productTemplate)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProductTemplate(templateId: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_CUSTOM_PRODUCTS)
                .document(templateId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getBusinessInsights(): Flow<Response<BusinessInsights>> = callbackFlow {
        trySend(Response.Loading)
        try {
            // Get all orders
            val ordersSnapshot = firestore.collection(COLLECTION_ORDERS)
                .get()
                .await()

            val orders = ordersSnapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data ?: return@mapNotNull null
                    Order(
                        orderId = document.id,
                        customerId = data["customerId"] as? String ?: "",
                        designId = data["designId"] as? String ?: "",
                        status = data["status"] as? String ?: "",
                        totalAmount = (data["totalAmount"] as? Number)?.toDouble() ?: 0.0,
                        createdAt = data["createdAt"] as? Timestamp
                    )
                } catch (e: Exception) {
                    null
                }
            }

            // Get product templates for stock calculation
            val templatesSnapshot = firestore.collection(COLLECTION_CUSTOM_PRODUCTS)
                .get()
                .await()

            // Get unique buyers
            val buyersSnapshot = firestore.collection(COLLECTION_USERS)
                .whereEqualTo("role", "customer")
                .get()
                .await()

            val totalOrders = orders.size
            val totalSales = orders.sumOf { it.totalAmount }
            val totalVisitors = 350 // This would need to be implemented with analytics
            val uniqueBuyers = orders.map { it.customerId }.distinct().size
            val totalStock = templatesSnapshot.size() * 10 // Assuming average stock per template

            val insights = BusinessInsights(
                totalOrders = totalOrders,
                totalSales = totalSales,
                totalVisitors = totalVisitors,
                totalBuyers = uniqueBuyers,
                totalStock = totalStock
            )

            trySend(Response.Success(insights))
            close()
        } catch (e: Exception) {
            trySend(Response.Error(e.message ?: "Failed to fetch business insights"))
            close()
        }
        awaitClose { }
    }

    override fun getSellingProductData(period: String): Flow<Response<List<ChartData>>> =
        callbackFlow {
            trySend(Response.Loading)
            try {
                val calendar = Calendar.getInstance()
                val endDate = calendar.time

                // Calculate start date based on period
                when (period) {
                    "week" -> calendar.add(Calendar.DAY_OF_YEAR, -7)
                    "month" -> calendar.add(Calendar.MONTH, -1)
                    "year" -> calendar.add(Calendar.YEAR, -1)
                }
                val startDate = calendar.time

                val ordersSnapshot = firestore.collection(COLLECTION_ORDERS)
                    .whereGreaterThanOrEqualTo("createdAt", Timestamp(startDate))
                    .whereLessThanOrEqualTo("createdAt", Timestamp(endDate))
                    .get()
                    .await()

                // Group orders by day/month based on period and calculate sales
                val chartData = mutableListOf<ChartData>()

                if (period == "week") {
                    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val salesByDay = mutableMapOf<String, Double>()

                    daysOfWeek.forEach { day ->
                        salesByDay[day] = (1000..8000).random().toDouble() // Mock data for demo
                    }

                    daysOfWeek.forEach { day ->
                        chartData.add(ChartData(day, salesByDay[day]?.toFloat() ?: 0f))
                    }
                } else {
                    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                    months.forEach { month ->
                        chartData.add(ChartData(month, (2000..6000).random().toFloat()))
                    }
                }

                trySend(Response.Success(chartData))
                close()
            } catch (e: Exception) {
                trySend(Response.Error(e.message ?: "Failed to fetch selling data"))
                close()
            }
            awaitClose { }
        }

    override fun getSalesRevenue(): Flow<Response<Double>> = callbackFlow {
        trySend(Response.Loading)
        try {
            val ordersSnapshot = firestore.collection(COLLECTION_ORDERS)
                .whereIn("status", listOf("delivered"))
                .get()
                .await()

            val totalRevenue = ordersSnapshot.documents.sumOf { document ->
                val data = document.data ?: return@sumOf 0.0
                (data["totalAmount"] as? Number)?.toDouble() ?: 0.0
            }

            trySend(Response.Success(totalRevenue))
            close()
        } catch (e: Exception) {
            trySend(Response.Error(e.message ?: "Failed to fetch sales revenue"))
            close()
        }
        awaitClose { }
    }

    override fun getOrderStatusCounts(): Flow<Response<OrderStatusCounts>> = callbackFlow {
        trySend(Response.Loading)
        try {
            val ordersSnapshot = firestore.collection(COLLECTION_ORDERS)
                .get()
                .await()

            val statusCounts = OrderStatusCounts(
                toShip = ordersSnapshot.documents.count { doc ->
                    (doc.data?.get("status") as? String) in listOf("processing")
                },
                shipped = ordersSnapshot.documents.count { doc ->
                    (doc.data?.get("status") as? String) == "shipped"
                },
                unpaid = ordersSnapshot.documents.count { doc ->
                    (doc.data?.get("status") as? String) == "pending"
                },
                success = ordersSnapshot.documents.count { doc ->
                    (doc.data?.get("status") as? String) == "delivered"
                }
            )

            trySend(Response.Success(statusCounts))
            close()
        } catch (e: Exception) {
            trySend(Response.Error(e.message ?: "Failed to fetch order status counts"))
            close()
        }
        awaitClose { }
    }

    override fun getLastOrders(limit: Int): Flow<Response<List<Order>>> = callbackFlow {
        trySend(Response.Loading)

        try {
            val snapshot = firestore.collection(COLLECTION_ORDERS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val orders = coroutineScope {
                snapshot.documents.mapNotNull { doc ->
                    async {
                        val order = doc.toObject(Order::class.java)?.copy(orderId = doc.id)
                            ?: return@async null

                        val customerDeferred = async { fetchCustomerData(order.customerId) }
                        val courierDeferred = async { fetchCourierData(order.courierId) }
                        val designDeferred = async { fetchDesignData(order.designId) }

                        val (customerName, customerAvatar) = customerDeferred.await()
                        val (courierName, courierLogo) = courierDeferred.await()
                        val (designImage, designTitle) = designDeferred.await()

                        order.copy(
                            customerName = customerName,
                            customerAvatarUrl = customerAvatar,
                            courierInfo = courierName,
                            courierLogo = courierLogo,
                            designImageUrl = designImage,
                            title = designTitle
                        )
                    }
                }.awaitAll().filterNotNull()
            }

            trySend(Response.Success(orders))
        } catch (e: Exception) {
            trySend(Response.Error(e.message ?: "Failed to fetch last orders"))
        }

        awaitClose { }
    }

    override fun getOrdersByStatus(status: String): Flow<Response<List<Order>>> = callbackFlow {
        trySend(Response.Loading)

        try {
            val statusFilter = when (status.lowercase()) {
                "pending" -> listOf("pending")
                "processing" -> listOf("processing")
                "shipped" -> listOf("shipped")
                "delivered" -> listOf("delivered")
                else -> listOf(status)
            }

            val snapshot = firestore.collection(COLLECTION_ORDERS)
                .whereIn("status", statusFilter)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = coroutineScope {
                snapshot.documents.mapNotNull { doc ->
                    async {
                        val order = doc.toObject(Order::class.java)?.copy(orderId = doc.id)
                            ?: return@async null

                        val customerDeferred = async { fetchCustomerData(order.customerId) }
                        val courierDeferred = async { fetchCourierData(order.courierId) }
                        val designDeferred = async { fetchDesignData(order.designId) }

                        val (customerName, customerAvatar) = customerDeferred.await()
                        val (courierName, courierLogo) = courierDeferred.await()
                        val (designImage, designTitle) = designDeferred.await()

                        order.copy(
                            customerName = customerName,
                            customerAvatarUrl = customerAvatar,
                            courierInfo = courierName,
                            courierLogo = courierLogo,
                            designImageUrl = designImage,
                            title = designTitle
                        )
                    }
                }.awaitAll().filterNotNull()
            }

            trySend(Response.Success(orders))
        } catch (e: Exception) {
            trySend(Response.Error(e.message ?: "Failed to fetch orders by status"))
        }

        awaitClose { }
    }

    override fun updateOrderStatus(
        orderId: String,
        status: String,
        courierId: String?,
        noResi: String?
    ): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)

            val updateData = mutableMapOf<String, Any>(
                "status" to status
            )

            courierId?.let { updateData["courierId"] = it }
            noResi?.let { updateData["noResi"] = it }

            firestore.collection(COLLECTION_ORDERS)
                .document(orderId)
                .update(updateData)
                .await()

            val order = firestore.collection(COLLECTION_ORDERS)
                .document(orderId).get().await().toObject(Order::class.java)

            val productName =
                order?.cartProducts?.firstOrNull()?.orderItem?.designType?.product?.name?.let {
                    "${order.cartProducts.firstOrNull()?.orderItem?.designType?.product?.name}${
                        if (order.cartProducts.size > 1) " and ${order.cartProducts.size - 1} more" else ""
                    }"
                } ?: "Custom Product"

            when (status) {
                "shipped" -> notificationRepository.publishOrderShipped(
                    order?.customerId ?: throw Exception("Customer ID not found"), productName
                )
                "delivered" -> notificationRepository.publishOrderDelivered(
                    order?.customerId ?: throw Exception("Customer ID not found"), productName
                )
            }

            emit(Response.Success("Order status updated successfully"))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to update order status"))
        }
    }

    override fun getOrderById(orderId: String): Flow<Response<Order>> = flow {
        emit(Response.Loading)

        try {
            val doc = firestore.collection(COLLECTION_ORDERS)
                .document(orderId)
                .get()
                .await()

            if (!doc.exists()) {
                emit(Response.Error("Order not found"))
                return@flow
            }

            val baseOrder = doc.toObject(Order::class.java)?.copy(orderId = doc.id)
            if (baseOrder == null) {
                emit(Response.Error("Failed to parse order data"))
                return@flow
            }

            val enrichedOrder = coroutineScope {
                val customerDeferred = async { fetchCustomerData(baseOrder.customerId) }
                val courierDeferred = async { fetchCourierData(baseOrder.courierId) }
                val designDeferred = async { fetchDesignData(baseOrder.designId) }

                val (customerName, customerAvatar) = customerDeferred.await()
                val (courierName, courierLogo) = courierDeferred.await()
                val (designImage, designTitle) = designDeferred.await()

                baseOrder.copy(
                    customerName = customerName,
                    customerAvatarUrl = customerAvatar,
                    courierInfo = courierName,
                    courierLogo = courierLogo,
                    designImageUrl = designImage,
                    title = designTitle
                )
            }

            emit(Response.Success(enrichedOrder))

        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to fetch order details"))
        }
    }

    override fun getUserById(userId: String): Flow<Response<User>> = flow {
        try {
            emit(Response.Loading)

            val userSnapshot = firestore.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .await()

            if (userSnapshot.exists()) {
                val user = userSnapshot.toObject(User::class.java)
                if (user != null) {
                    emit(Response.Success(user))
                } else {
                    emit(Response.Error("User data is corrupted"))
                }
            } else {
                emit(Response.Error("User not found"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to get user details"))
        }
    }

    override fun getTransactionHistory(filter: TransactionFilter?): Flow<Response<List<Transaction>>> =
        callbackFlow {
            trySend(Response.Loading)
            try {
                // Get completed orders to create payment transactions
                firestore.collection(COLLECTION_ORDERS)
                    .whereIn("status", listOf("completed", "paid", "processing"))
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { ordersSnapshot ->
                        val transactions = mutableListOf<Transaction>()

                        // Create payment transactions from orders
                        var processedCount = 0
                        val totalDocuments = ordersSnapshot.documents.size


                        for (document in ordersSnapshot.documents) {
                            val data = document.data
                            if (data == null) {
                                processedCount++
                                if (processedCount == totalDocuments) {
                                    val filteredTransactions =
                                        applyTransactionFilters(transactions, filter)
                                    trySend(Response.Success(filteredTransactions))
                                    close()
                                }
                                continue
                            }

                            val customerId = data["customerId"] as? String ?: ""
                            val totalAmount = (data["totalAmount"] as? Number)?.toDouble() ?: 0.0
                            val createdAt = data["createdAt"] as? Timestamp

                            // Get customer name
                            firestore.collection(COLLECTION_USERS)
                                .document(customerId)
                                .get()
                                .addOnSuccessListener { userSnapshot ->
                                    val userData = userSnapshot.data
                                    val customerName = userData?.get("fullName") as? String
                                        ?: userData?.get("name") as? String
                                        ?: "Unknown Customer"

                                    val transaction = Transaction(
                                        id = document.id,
                                        type = TransactionType.PAYMENT,
                                        amount = totalAmount,
                                        customerName = customerName,
                                        customerId = customerId,
                                        orderId = document.id,
                                        description = "Payment by $customerName",
                                        createdAt = createdAt
                                    )

                                    transactions.add(transaction)
                                    processedCount++

                                    if (processedCount == totalDocuments) {
                                        // Add mock withdrawal transactions for demo
                                        transactions.add(
                                            Transaction(
                                                id = "withdrawal_1",
                                                type = TransactionType.WITHDRAWAL,
                                                amount = 200000.0,
                                                customerName = "",
                                                customerId = "",
                                                orderId = null,
                                                description = "Withdrawal",
                                                createdAt = Timestamp.now()
                                            )
                                        )

                                        // Apply filters and send result
                                        val filteredTransactions =
                                            applyTransactionFilters(transactions, filter)
                                        trySend(Response.Success(filteredTransactions))
                                        close()
                                    }
                                }
                                .addOnFailureListener {
                                    // Use default name if user fetch fails
                                    val transaction = Transaction(
                                        id = document.id,
                                        type = TransactionType.PAYMENT,
                                        amount = totalAmount,
                                        customerName = "Unknown Customer",
                                        customerId = customerId,
                                        orderId = document.id,
                                        description = "Payment by Unknown Customer",
                                        createdAt = createdAt
                                    )

                                    transactions.add(transaction)
                                    processedCount++

                                    if (processedCount == totalDocuments) {
                                        // Add mock withdrawal transactions for demo
                                        transactions.add(
                                            Transaction(
                                                id = "withdrawal_1",
                                                type = TransactionType.WITHDRAWAL,
                                                amount = 200000.0,
                                                customerName = "",
                                                customerId = "",
                                                orderId = null,
                                                description = "Withdrawal",
                                                createdAt = Timestamp.now()
                                            )
                                        )

                                        // Apply filters and send result
                                        val filteredTransactions =
                                            applyTransactionFilters(transactions, filter)
                                        trySend(Response.Success(filteredTransactions))
                                        close()
                                    }
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        trySend(Response.Error(e.message ?: "Failed to fetch transaction history"))
                        close()
                    }
            } catch (e: Exception) {
                trySend(Response.Error(e.message ?: "Failed to fetch transaction history"))
                close()
            }
            awaitClose { }
        }

    private fun applyTransactionFilters(
        transactions: List<Transaction>,
        filter: TransactionFilter?
    ): List<Transaction> {
        var filteredTransactions = transactions

        filter?.let { f ->
            // Filter by transaction type
            if (f.type != null) {
                filteredTransactions = filteredTransactions.filter { it.type == f.type }
            }

            // Filter by date range
            if (f.startDate != null && f.endDate != null) {
                filteredTransactions = filteredTransactions.filter { transaction ->
                    transaction.createdAt?.let { createdAt ->
                        createdAt.seconds >= f.startDate.seconds && createdAt.seconds <= f.endDate.seconds
                    } ?: false
                }
            }
        }

        // Sort by date descending
        return filteredTransactions.sortedByDescending { it.createdAt?.seconds ?: 0 }
    }

    override fun getTotalBalance(): Flow<Response<Double>> = callbackFlow {
        trySend(Response.Loading)
        try {
            firestore.collection(COLLECTION_ORDERS)
                .whereIn("status", listOf("completed", "paid", "processing"))
                .get()
                .addOnSuccessListener { ordersSnapshot ->
                    val totalEarnings = ordersSnapshot.documents.sumOf { document ->
                        val data = document.data ?: return@sumOf 0.0
                        (data["totalAmount"] as? Number)?.toDouble() ?: 0.0
                    }

                    // Subtract mock withdrawals (in a real app, you'd track these in a separate collection)
                    val totalWithdrawals = 200000.0
                    val currentBalance = totalEarnings - totalWithdrawals

                    trySend(Response.Success(currentBalance))
                    close()
                }
                .addOnFailureListener { e ->
                    trySend(Response.Error(e.message ?: "Failed to fetch total balance"))
                    close()
                }
        } catch (e: Exception) {
            trySend(Response.Error(e.message ?: "Failed to fetch total balance"))
            close()
        }
        awaitClose { }
    }

    override fun getCustomers(searchQuery: String): Flow<Response<List<User>>> = callbackFlow {
        trySend(Response.Loading)
        try {
            var query = firestore.collection(COLLECTION_USERS)
                .whereEqualTo("role", "customer")

            val snapshot = query.get().await()

            val customers = snapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data ?: return@mapNotNull null
                    User(
                        id = document.id,
                        name = data["name"] as? String ?: data["fullName"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        profilePicture = data["profilePicture"] as? String ?: "",
                        role = data["role"] as? String ?: "customer",
                        phone = data["phone"] as? String ?: "",
                        createdAt = data["createdAt"] as? Timestamp ?: Timestamp.now()
                    )
                } catch (e: Exception) {
                    null
                }
            }

            // Apply search filter if provided
            val filteredCustomers = if (searchQuery.isNotEmpty()) {
                customers.filter { customer ->
                    customer.name?.contains(searchQuery, ignoreCase = true) == true ||
                            customer.email?.contains(searchQuery, ignoreCase = true) == true ||
                            customer.phone.contains(searchQuery, ignoreCase = true)
                }
            } else {
                customers
            }

            trySend(Response.Success(filteredCustomers))
            close()
        } catch (e: Exception) {
            trySend(Response.Error(e.message ?: "Failed to fetch customers"))
            close()
        }
        awaitClose { }
    }

    private suspend fun fetchCustomerData(customerId: String): Pair<String, String> {
        if (customerId.isEmpty()) return "Unknown Customer" to ""
        return try {
            val snapshot = firestore
                .collection(COLLECTION_USERS)
                .document(customerId)
                .get()
                .await()

            val data = snapshot.data ?: return "Unknown Customer" to ""
            val name = data["fullName"] as? String
                ?: data["name"] as? String
                ?: "Unknown Customer"
            val avatar = data["avatarUrl"] as? String
                ?: data["profilePicture"] as? String
                ?: ""
            name to avatar
        } catch (e: Exception) {
            "Unknown Customer" to ""
        }
    }

    private suspend fun fetchCourierData(courierId: String?): Pair<String?, String?> {
        if (courierId.isNullOrEmpty()) return null to null
        return try {
            val snapshot = firestore
                .collection(COLLECTION_EKSPEDISI)
                .document(courierId)
                .get()
                .await()

            val data = snapshot.data ?: return null to null
            (data["name"] as? String) to (data["logo"] as? String)
        } catch (e: Exception) {
            null to null
        }
    }

    private suspend fun fetchDesignData(designId: String): Pair<String, String> {
        if (designId.isEmpty()) return "" to "Custom Order"
        return try {
            val snapshot = firestore
                .collection(COLLECTION_CUSTOM_DESIGNS)
                .document(designId)
                .get()
                .await()

            val data = snapshot.data ?: return "" to "Custom Order"
            val imageUrl = data["imageUrl"] as? String ?: ""
            val title = data["title"] as? String ?: "Custom Design"
            imageUrl to title
        } catch (e: Exception) {
            "" to "Custom Order"
        }
    }
}
