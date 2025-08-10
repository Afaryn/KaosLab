package com.afaryn.kaoslab.data

import com.afaryn.kaoslab.model.ProductTemplate
import com.afaryn.kaoslab.model.SizeOption
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OwnerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : OwnerRepository {

    companion object {
        private const val COLLECTION_CUSTOM_PRODUCTS = "customproduct"
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

    override suspend fun addProductTemplate(productTemplate: ProductTemplate): Result<String> {
        return try {
            val document = firestore.collection(COLLECTION_CUSTOM_PRODUCTS)
                .add(productTemplate)
                .await()
            Result.success(document.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
}
