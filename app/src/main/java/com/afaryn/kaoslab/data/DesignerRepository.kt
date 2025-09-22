package com.afaryn.kaoslab.data

import android.net.Uri
import com.afaryn.kaoslab.model.ProductTemplate
import com.afaryn.kaoslab.model.BusinessInsights
import com.afaryn.kaoslab.model.ChartData
import com.afaryn.kaoslab.model.Order
import com.afaryn.kaoslab.model.OrderStatusCounts
import com.afaryn.kaoslab.model.Transaction
import com.afaryn.kaoslab.model.TransactionFilter
import com.afaryn.kaoslab.model.Design
import com.afaryn.kaoslab.model.Portfolio
import com.afaryn.kaoslab.model.User
import com.afaryn.kaoslab.utils.Response
import kotlinx.coroutines.flow.Flow

interface DesignerRepository {
    // Design Management methods
    fun getDesigns(): Flow<Response<List<Design>>>
    fun addDesign(design: Design): Flow<Response<String>>
    fun updateDesign(design: Design): Flow<Response<String>>
    fun deleteDesign(designId: String): Flow<Response<String>>
    fun getDesignById(designId: String): Flow<Response<Design>>

    // Portfolio Management methods
    fun getPortfolios(): Flow<Response<List<Portfolio>>>
    fun addPortfolio(portfolio: Portfolio): Flow<Response<String>>
    fun updatePortfolio(portfolio: Portfolio): Flow<Response<String>>
    fun deletePortfolio(portfolioId: String): Flow<Response<String>>
    fun getPortfolioById(portfolioId: String): Flow<Response<Portfolio>>

    // Profile Management methods
    fun getCurrentUser(): Flow<Response<User>>
    fun updateUserProfile(user: User): Flow<Response<String>>

    // Image Upload methods
    suspend fun uploadDesignImage(imageUri: Uri, designId: String): String
    suspend fun uploadPortfolioImage(imageUri: Uri, portfolioId: String): String
    suspend fun uploadProfileImage(imageUri: Uri, userId: String): String
    suspend fun deleteImageFromStorage(imageUrl: String)
}
