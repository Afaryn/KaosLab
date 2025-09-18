package com.afaryn.kaoslab.data

import com.afaryn.kaoslab.model.User
import com.afaryn.kaoslab.model.Design
import com.afaryn.kaoslab.utils.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DesignerRepositoryImpl @Inject constructor(
    val auth: FirebaseAuth,
    val firestore: FirebaseFirestore
): DesignerRepository {

    override fun getDesigns(): Flow<Response<List<Design>>> = flow {
        try {
            emit(Response.Loading)
            val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            // First try to get documents without orderBy to avoid index issues on new collections
            val snapshot = try {
                firestore.collection("designs")
                    .whereEqualTo("designerId", currentUserId)
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()
            } catch (indexException: Exception) {
                if (indexException.message?.contains("index") == true) {
                    // If index doesn't exist yet, fall back to simple query without ordering
                    android.util.Log.w("DesignerRepository", "Index not found, using simple query")
                    firestore.collection("designs")
                        .whereEqualTo("designerId", currentUserId)
                        .get()
                        .await()
                } else {
                    throw indexException
                }
            }

            // Handle empty collection gracefully
            val designs = if (snapshot.isEmpty) {
                emptyList<Design>()
            } else {
                val designList = snapshot.toObjects(Design::class.java).filterNotNull()
                // Sort manually if we had to use simple query
                designList.sortedByDescending { it.createdAt }
            }

            emit(Response.Success(designs))
        } catch (e: Exception) {
            // Log the error for debugging
            android.util.Log.e("DesignerRepository", "Error getting designs", e)

            // Check if it's a collection not found error or other Firestore errors
            when {
                e.message?.contains("not found") == true -> {
                    // Collection doesn't exist yet, return empty list
                    emit(Response.Success(emptyList()))
                }
                e.message?.contains("permission") == true -> {
                    emit(Response.Error("Permission denied. Please check your authentication."))
                }
                e.message?.contains("network") == true || e.message?.contains("offline") == true -> {
                    emit(Response.Error("Network error. Please check your internet connection."))
                }
                e.message?.contains("index") == true -> {
                    // Firestore index not created yet, but still try to return empty for now
                    android.util.Log.w("DesignerRepository", "Firestore index not ready, returning empty list")
                    emit(Response.Success(emptyList()))
                }
                else -> {
                    emit(Response.Error(e.message ?: "Failed to get designs"))
                }
            }
        }
    }

    override fun addDesign(design: Design): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            val designId = firestore.collection("designs").document().id
            val designWithId = design.copy(
                id = designId,
                designerId = currentUserId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            firestore.collection("designs")
                .document(designId)
                .set(designWithId)
                .await()

            emit(Response.Success("Design added successfully"))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to add design"))
        }
    }

    override fun updateDesign(design: Design): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            if (design.designerId != currentUserId) {
                throw Exception("Unauthorized to update this design")
            }

            val updatedDesign = design.copy(updatedAt = System.currentTimeMillis())

            firestore.collection("designs")
                .document(design.id)
                .set(updatedDesign)
                .await()

            emit(Response.Success("Design updated successfully"))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to update design"))
        }
    }

    override fun deleteDesign(designId: String): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)

            firestore.collection("designs")
                .document(designId)
                .delete()
                .await()

            emit(Response.Success("Design deleted successfully"))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to delete design"))
        }
    }

    override fun getDesignById(designId: String): Flow<Response<Design>> = flow {
        try {
            emit(Response.Loading)

            val snapshot = firestore.collection("designs")
                .document(designId)
                .get()
                .await()

            val design = snapshot.toObject(Design::class.java)
            if (design != null) {
                emit(Response.Success(design))
            } else {
                emit(Response.Error("Design not found"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to get design"))
        }
    }
}