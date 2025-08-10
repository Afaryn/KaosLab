package com.afaryn.kaoslab.data

import com.afaryn.kaoslab.model.ProductTemplate
import kotlinx.coroutines.flow.Flow

interface OwnerRepository {
    fun getProductTemplates(): Flow<List<ProductTemplate>>
    suspend fun addProductTemplate(productTemplate: ProductTemplate): Result<String>
    suspend fun updateProductTemplate(productTemplate: ProductTemplate): Result<Unit>
    suspend fun deleteProductTemplate(templateId: String): Result<Unit>
}
