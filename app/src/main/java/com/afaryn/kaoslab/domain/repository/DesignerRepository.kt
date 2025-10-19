package com.afaryn.kaoslab.domain.repository

import android.net.Uri
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.domain.model.DesignOrder
import com.afaryn.kaoslab.domain.model.DesignOrderStatus
import com.afaryn.kaoslab.domain.model.Portfolio
import com.afaryn.kaoslab.domain.model.User
import com.afaryn.kaoslab.utils.Resource
import com.afaryn.kaoslab.utils.Response
import kotlinx.coroutines.flow.Flow

interface DesignerRepository {
    // Design Management methods
    fun getDesigns(): Flow<Response<List<Design>>>
    fun addDesign(design: Design, imgUri: Uri): Flow<Response<String>>
    fun updateDesign(design: Design, imgUri: Uri? = null): Flow<Response<String>>
    fun deleteDesign(designId: String): Flow<Response<String>>
    fun getDesignById(designId: String): Flow<Response<Design>>
    fun getDesignSales(status: DesignOrderStatus): Flow<Resource<List<DesignOrder>>>

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
