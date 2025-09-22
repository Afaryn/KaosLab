package com.afaryn.kaoslab.data

import android.net.Uri
import com.afaryn.kaoslab.model.User
import com.afaryn.kaoslab.model.Design
import com.afaryn.kaoslab.model.Portfolio
import com.afaryn.kaoslab.utils.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DesignerRepositoryImpl @Inject constructor(
    val auth: FirebaseAuth,
    val firestore: FirebaseFirestore,
    val storage: FirebaseStorage
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

            if (snapshot.exists()) {
                val design = snapshot.toObject(Design::class.java)
                if (design != null) {
                    emit(Response.Success(design))
                } else {
                    emit(Response.Error("Design data is corrupted"))
                }
            } else {
                emit(Response.Error("Design not found"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to get design"))
        }
    }

    // Portfolio Management methods
    override fun getPortfolios(): Flow<Response<List<Portfolio>>> = flow {
        try {
            emit(Response.Loading)
            val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            val snapshot = try {
                firestore.collection("designerPortfolios")
                    .whereEqualTo("designerId", currentUserId)
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()
            } catch (indexException: Exception) {
                if (indexException.message?.contains("index") == true) {
                    android.util.Log.w("DesignerRepository", "Index not found for portfolios, using simple query")
                    firestore.collection("designerPortfolios")
                        .whereEqualTo("designerId", currentUserId)
                        .get()
                        .await()
                } else {
                    throw indexException
                }
            }

            val portfolios = if (snapshot.isEmpty) {
                emptyList<Portfolio>()
            } else {
                val portfolioList = snapshot.toObjects(Portfolio::class.java).filterNotNull()
                portfolioList.sortedByDescending { it.createdAt }
            }

            emit(Response.Success(portfolios))
        } catch (e: Exception) {
            android.util.Log.e("DesignerRepository", "Error getting portfolios", e)

            when {
                e.message?.contains("not found") == true -> {
                    emit(Response.Success(emptyList()))
                }
                e.message?.contains("permission") == true -> {
                    emit(Response.Error("Permission denied. Please check your authentication."))
                }
                e.message?.contains("network") == true || e.message?.contains("offline") == true -> {
                    emit(Response.Error("Network error. Please check your internet connection."))
                }
                e.message?.contains("index") == true -> {
                    android.util.Log.w("DesignerRepository", "Firestore index not ready for portfolios, returning empty list")
                    emit(Response.Success(emptyList()))
                }
                else -> {
                    emit(Response.Error(e.message ?: "Failed to get portfolios"))
                }
            }
        }
    }

    override fun addPortfolio(portfolio: Portfolio): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            val portfolioId = firestore.collection("designerPortfolios").document().id
            val portfolioWithId = portfolio.copy(
                id = portfolioId,
                designerId = currentUserId,
                createdAt = Timestamp.now()
            )

            firestore.collection("designerPortfolios")
                .document(portfolioId)
                .set(portfolioWithId)
                .await()

            emit(Response.Success("Portfolio added successfully"))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to add portfolio"))
        }
    }

    override fun updatePortfolio(portfolio: Portfolio): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            if (portfolio.designerId != currentUserId) {
                throw Exception("Unauthorized to update this portfolio")
            }

            firestore.collection("designerPortfolios")
                .document(portfolio.id)
                .set(portfolio)
                .await()

            emit(Response.Success("Portfolio updated successfully"))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to update portfolio"))
        }
    }

    override fun deletePortfolio(portfolioId: String): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)

            firestore.collection("designerPortfolios")
                .document(portfolioId)
                .delete()
                .await()

            emit(Response.Success("Portfolio deleted successfully"))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to delete portfolio"))
        }
    }

    override fun getPortfolioById(portfolioId: String): Flow<Response<Portfolio>> = flow {
        try {
            emit(Response.Loading)

            val snapshot = firestore.collection("designerPortfolios")
                .document(portfolioId)
                .get()
                .await()

            if (snapshot.exists()) {
                val portfolio = snapshot.toObject(Portfolio::class.java)
                if (portfolio != null) {
                    emit(Response.Success(portfolio))
                } else {
                    emit(Response.Error("Portfolio data is corrupted"))
                }
            } else {
                emit(Response.Error("Portfolio not found"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to get portfolio"))
        }
    }

    // Profile Management methods
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

    override fun updateUserProfile(user: User): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            if (user.id != currentUserId) {
                throw Exception("Unauthorized to update this profile")
            }

            firestore.collection("users")
                .document(currentUserId)
                .set(user)
                .await()

            emit(Response.Success("Profile updated successfully"))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to update profile"))
        }
    }

    // Image upload helper methods
    override suspend fun uploadDesignImage(imageUri: Uri, designId: String): String {
        val timestamp = System.currentTimeMillis()
        val imageName = "design_${designId}_$timestamp.jpg"
        val imageRef = storage.reference.child("designs/$imageName")

        return try {
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload design image: ${e.message}")
        }
    }

    override suspend fun uploadPortfolioImage(imageUri: Uri, portfolioId: String): String {
        val timestamp = System.currentTimeMillis()
        val imageName = "portfolio_${portfolioId}_$timestamp.jpg"
        val imageRef = storage.reference.child("portfolios/$imageName")

        return try {
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload portfolio image: ${e.message}")
        }
    }

    override suspend fun deleteImageFromStorage(imageUrl: String) {
        try {
            if (imageUrl.isNotEmpty() && imageUrl.contains("firebase")) {
                storage.getReferenceFromUrl(imageUrl).delete().await()
            }
        } catch (e: Exception) {
            // Log error but don't fail the operation
            android.util.Log.w("DesignerRepository", "Failed to delete image from storage: ${e.message}")
        }
    }

    override suspend fun uploadProfileImage(imageUri: Uri, userId: String): String {
        val timestamp = System.currentTimeMillis()
        val imageName = "profile_${userId}_$timestamp.jpg"
        val imageRef = storage.reference.child("profiles/$imageName")

        return try {
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload profile image: ${e.message}")
        }
    }
}