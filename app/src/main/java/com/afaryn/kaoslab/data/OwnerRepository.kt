package com.afaryn.kaoslab.data

import com.afaryn.kaoslab.model.ProductTemplate
import kotlinx.coroutines.flow.Flow

interface OwnerRepository {
    fun getProductTemplates(): Flow<List<ProductTemplate>>
    fun addProductTemplate(productTemplate: ProductTemplate): Flow<com.afaryn.kaoslab.utils.Response<String>>
    suspend fun updateProductTemplate(productTemplate: ProductTemplate): Result<Unit>
    suspend fun deleteProductTemplate(templateId: String): Result<Unit>
}
