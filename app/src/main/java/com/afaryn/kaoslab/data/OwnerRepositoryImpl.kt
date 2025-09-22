package com.afaryn.kaoslab.data

import com.afaryn.kaoslab.model.ProductTemplate
import com.afaryn.kaoslab.model.SizeOption
import com.afaryn.kaoslab.model.Order
import com.afaryn.kaoslab.model.BusinessInsights
import com.afaryn.kaoslab.model.ChartData
import com.afaryn.kaoslab.model.OrderStatusCounts
import com.afaryn.kaoslab.model.CustomDesign
import com.afaryn.kaoslab.model.Kurir
import com.afaryn.kaoslab.model.TransactionFilter
import com.afaryn.kaoslab.model.User
import com.afaryn.kaoslab.utils.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OwnerRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
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
                        createdAt = data["createdAt"] as? com.google.firebase.Timestamp
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
            if(snapshot != null) {
                val kurirData = snapshot.toObjects(Kurir::class.java) ?: throw Exception("No courier data found")
                emit(Response.Success(kurirData))
            } else {
                emit(Response.Error("No courier data found"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to fetch courier data"))
        }
    }

    override fun addProductTemplate(productTemplate: ProductTemplate): Flow<Response<String>> = callbackFlow {
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
                        createdAt = data["createdAt"] as? com.google.firebase.Timestamp
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

    override fun getSellingProductData(period: String): Flow<Response<List<ChartData>>> = callbackFlow {
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
                .whereGreaterThanOrEqualTo("createdAt", com.google.firebase.Timestamp(startDate))
                .whereLessThanOrEqualTo("createdAt", com.google.firebase.Timestamp(endDate))
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
                .whereIn("status", listOf("processing", "completed"))
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
                    (doc.data?.get("status") as? String) in listOf("processing", "paid")
                },
                cancelled = ordersSnapshot.documents.count { doc ->
                    (doc.data?.get("status") as? String) == "cancelled"
                },
                returned = ordersSnapshot.documents.count { doc ->
                    (doc.data?.get("status") as? String) == "returned"
                },
                review = ordersSnapshot.documents.count { doc ->
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
            val ordersSnapshot = firestore.collection(COLLECTION_ORDERS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val orders = mutableListOf<Order>()

            for (document in ordersSnapshot.documents) {
                val data = document.data ?: continue
                val customerId = data["customerId"] as? String ?: ""
                val designId = data["designId"] as? String ?: ""

                // Fetch customer data for name and avatar
                val customerName = try {
                    val userSnapshot = firestore.collection(COLLECTION_USERS)
                        .document(customerId)
                        .get()
                        .await()
                    val userData = userSnapshot.data
                    userData?.get("fullName") as? String ?: userData?.get("name") as? String ?: "Unknown Customer"
                } catch (e: Exception) {
                    "Unknown Customer"
                }

                val customerAvatarUrl = try {
                    val userSnapshot = firestore.collection(COLLECTION_USERS)
                        .document(customerId)
                        .get()
                        .await()
                    val userData = userSnapshot.data
                    userData?.get("avatarUrl") as? String ?: userData?.get("profilePicture") as? String ?: ""
                } catch (e: Exception) {
                    ""
                }

                // Fetch design details from customDesigns collection
                val (designImageUrl, designTitle) = if (designId.isNotEmpty()) {
                    try {
                        val designSnapshot = firestore.collection(COLLECTION_CUSTOM_DESIGNS)
                            .document(designId)
                            .get()
                            .await()

                        val designData = designSnapshot.data
                        if (designData != null) {
                            val imageUrl = designData["imageUrl"] as? String ?: ""
                            val title = "Custom Design" // You can get more specific title if available
                            Pair(imageUrl, title)
                        } else {
                            Pair("", "Custom Order")
                        }
                    } catch (e: Exception) {
                        Pair("", "Custom Order")
                    }
                } else {
                    Pair("", "Custom Order")
                }

                val order = Order(
                    orderId = document.id,
                    customerId = customerId,
                    customerName = customerName,
                    customerAvatarUrl = customerAvatarUrl,
                    designId = designId,
                    status = data["status"] as? String ?: "",
                    totalAmount = (data["totalAmount"] as? Number)?.toDouble() ?: 0.0,
                    totalPieces = (data["totalPieces"] as? Number)?.toInt() ?: 1,
                    size = data["size"] as? String ?: "M",
                    title = designTitle,
                    designImageUrl = designImageUrl,
                    courierInfo = data["courierInfo"] as? String,
                    courierLogo = data["courierLogo"] as? String,
                    trackingNumber = data["trackingNumber"] as? String,
                    createdAt = data["createdAt"] as? com.google.firebase.Timestamp
                )
                orders.add(order)
            }

            trySend(Response.Success(orders))
            close()
        } catch (e: Exception) {
            trySend(Response.Error(e.message ?: "Failed to fetch last orders"))
            close()
        }
        awaitClose { }
    }

    override fun getOrdersByStatus(status: String): Flow<Response<List<Order>>> = callbackFlow {
        trySend(Response.Loading)
        try {
            val statusFilter = when (status) {
                "unpaid" -> listOf("pending", "unpaid")
                "to_deliver" -> listOf("processing", "paid", "to_deliver")
                "shipping" -> listOf("shipped", "shipping")
                "completed" -> listOf("delivered", "completed")
                else -> listOf(status)
            }

            val ordersSnapshot = firestore.collection(COLLECTION_ORDERS)
                .whereIn("status", statusFilter)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = mutableListOf<Order>()

            for (document in ordersSnapshot.documents) {
                val data = document.data ?: continue
                val customerId = data["customerId"] as? String ?: ""
                val designId = data["designId"] as? String ?: ""

                // Fetch customer data for name and avatar
                val customerName = try {
                    val userSnapshot = firestore.collection(COLLECTION_USERS)
                        .document(customerId)
                        .get()
                        .await()
                    val userData = userSnapshot.data
                    userData?.get("fullName") as? String ?: userData?.get("name") as? String ?: "Unknown Customer"
                } catch (e: Exception) {
                    "Unknown Customer"
                }

                val customerAvatarUrl = try {
                    val userSnapshot = firestore.collection(COLLECTION_USERS)
                        .document(customerId)
                        .get()
                        .await()
                    val userData = userSnapshot.data
                    userData?.get("avatarUrl") as? String ?: userData?.get("profilePicture") as? String ?: ""
                } catch (e: Exception) {
                    ""
                }

                // Fetch design details from customDesigns collection
                val (designImageUrl, designTitle) = if (designId.isNotEmpty()) {
                    try {
                        val designSnapshot = firestore.collection(COLLECTION_CUSTOM_DESIGNS)
                            .document(designId)
                            .get()
                            .await()

                        val designData = designSnapshot.data
                        if (designData != null) {
                            val imageUrl = designData["imageUrl"] as? String ?: ""
                            val title = "Custom Design"
                            Pair(imageUrl, title)
                        } else {
                            Pair("", "Custom Order")
                        }
                    } catch (e: Exception) {
                        Pair("", "Custom Order")
                    }
                } else {
                    Pair("", "Custom Order")
                }

                val order = Order(
                    orderId = document.id,
                    customerId = customerId,
                    customerName = customerName,
                    customerAvatarUrl = customerAvatarUrl,
                    designId = designId,
                    status = data["status"] as? String ?: "",
                    totalAmount = (data["totalAmount"] as? Number)?.toDouble() ?: 0.0,
                    totalPieces = (data["totalPieces"] as? Number)?.toInt() ?: 1,
                    size = data["size"] as? String ?: "M",
                    title = designTitle,
                    designImageUrl = designImageUrl,
                    courierInfo = data["courierInfo"] as? String,
                    courierLogo = data["courierLogo"] as? String,
                    trackingNumber = data["trackingNumber"] as? String,
                    createdAt = data["createdAt"] as? com.google.firebase.Timestamp
                )
                orders.add(order)
            }

            trySend(Response.Success(orders))
            close()
        } catch (e: Exception) {
            trySend(Response.Error(e.message ?: "Failed to fetch orders by status"))
            close()
        }
        awaitClose { }
    }

    override fun getTransactionHistory(filter: TransactionFilter?): Flow<Response<List<com.afaryn.kaoslab.model.Transaction>>> = callbackFlow {
        trySend(Response.Loading)
        try {
            // Get completed orders to create payment transactions
            firestore.collection(COLLECTION_ORDERS)
                .whereIn("status", listOf("completed", "paid", "processing"))
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { ordersSnapshot ->
                    val transactions = mutableListOf<com.afaryn.kaoslab.model.Transaction>()

                    // Create payment transactions from orders
                    var processedCount = 0
                    val totalDocuments = ordersSnapshot.documents.size


                    for (document in ordersSnapshot.documents) {
                        val data = document.data
                        if (data == null) {
                            processedCount++
                            if (processedCount == totalDocuments) {
                                val filteredTransactions = applyTransactionFilters(transactions, filter)
                                trySend(Response.Success(filteredTransactions))
                                close()
                            }
                            continue
                        }

                        val customerId = data["customerId"] as? String ?: ""
                        val totalAmount = (data["totalAmount"] as? Number)?.toDouble() ?: 0.0
                        val createdAt = data["createdAt"] as? com.google.firebase.Timestamp

                        // Get customer name
                        firestore.collection(COLLECTION_USERS)
                            .document(customerId)
                            .get()
                            .addOnSuccessListener { userSnapshot ->
                                val userData = userSnapshot.data
                                val customerName = userData?.get("fullName") as? String
                                    ?: userData?.get("name") as? String
                                    ?: "Unknown Customer"

                                val transaction = com.afaryn.kaoslab.model.Transaction(
                                    id = document.id,
                                    type = com.afaryn.kaoslab.model.TransactionType.PAYMENT,
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
                                        com.afaryn.kaoslab.model.Transaction(
                                            id = "withdrawal_1",
                                            type = com.afaryn.kaoslab.model.TransactionType.WITHDRAWAL,
                                            amount = 200000.0,
                                            customerName = "",
                                            customerId = "",
                                            orderId = null,
                                            description = "Withdrawal",
                                            createdAt = com.google.firebase.Timestamp.now()
                                        )
                                    )

                                    // Apply filters and send result
                                    val filteredTransactions = applyTransactionFilters(transactions, filter)
                                    trySend(Response.Success(filteredTransactions))
                                    close()
                                }
                            }
                            .addOnFailureListener {
                                // Use default name if user fetch fails
                                val transaction = com.afaryn.kaoslab.model.Transaction(
                                    id = document.id,
                                    type = com.afaryn.kaoslab.model.TransactionType.PAYMENT,
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
                                        com.afaryn.kaoslab.model.Transaction(
                                            id = "withdrawal_1",
                                            type = com.afaryn.kaoslab.model.TransactionType.WITHDRAWAL,
                                            amount = 200000.0,
                                            customerName = "",
                                            customerId = "",
                                            orderId = null,
                                            description = "Withdrawal",
                                            createdAt = com.google.firebase.Timestamp.now()
                                        )
                                    )

                                    // Apply filters and send result
                                    val filteredTransactions = applyTransactionFilters(transactions, filter)
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
        transactions: List<com.afaryn.kaoslab.model.Transaction>,
        filter: TransactionFilter?
    ): List<com.afaryn.kaoslab.model.Transaction> {
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
                        createdAt = data["createdAt"] as? com.google.firebase.Timestamp ?: com.google.firebase.Timestamp.now()
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
}
