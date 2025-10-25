package com.afaryn.kaoslab.data.repository

import com.afaryn.kaoslab.domain.model.User
import com.afaryn.kaoslab.domain.repository.AuthRepository
import com.afaryn.kaoslab.utils.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    val auth: FirebaseAuth,
    val firestore: FirebaseFirestore
): AuthRepository {
    override fun login(email: String, password: String): Flow<Response<User>> =
        flow {
            emit(Response.Loading)
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user ?: throw Exception("User is null")

                // TODO: UNCOMMENT EMAIL VERIFICATION
                // Check if email is verified
//                if (!user.isEmailVerified) {
//                    // Sign out the user since email is not verified
//                    auth.signOut()
//                    emit(Response.Error("EMAIL_NOT_VERIFIED"))
//                    return@flow
//                }

                val userId = user.uid
                val userDocument = firestore.collection("users").document(userId).get().await()
                val userData = userDocument.toObject(User::class.java) ?: throw Exception("User not found")
                userData.id = userId
                emit(Response.Success(userData))
            } catch (e: Exception) {
                emit(Response.Error(e.message ?: "An error occurred"))
            }
        }

    override fun register(email: String, password: String, user: User): Flow<Response<String>> =
        flow {
            emit(Response.Loading)
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = result.user?.uid ?: throw Exception("User ID is null")
                user.id = userId
                firestore.collection("users").document(userId).set(user).await()

                // Send email verification after successful registration
                result.user?.sendEmailVerification()?.await()

                emit(Response.Success("User registered successfully"))
            } catch (e: Exception) {
                emit(Response.Error(e.message ?: "An error occurred"))
            }
        }

    override fun sendEmailVerification(): Flow<Response<String>> = flow {
        emit(Response.Loading)
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                currentUser.sendEmailVerification().await()
                emit(Response.Success("Email verification sent successfully"))
            } else {
                emit(Response.Error("No user logged in"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to send email verification"))
        }
    }

    override fun resendEmailVerification(email: String, password: String): Flow<Response<String>> = flow {
        emit(Response.Loading)
        try {
            // Sign in the user temporarily to send verification
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User is null")

            // Send email verification
            user.sendEmailVerification().await()

            // Sign out the user since email is not verified
            auth.signOut()

            emit(Response.Success("Email verification sent successfully"))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to send email verification"))
        }
    }

    override fun resetPassword(email: String): Flow<Response<String>> = flow {
        emit(Response.Loading)
        try {
            auth.sendPasswordResetEmail(email).await()
            emit(Response.Success("Password reset email sent successfully"))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to send password reset email"))
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun userUid(): String = auth.currentUser?.uid ?: ""

    override fun isLoggedIn(): Flow<Boolean> = flow {
        emit(auth.uid != null)
    }

    override fun getCurrentUser(): Flow<Response<User>> = flow {
        emit(Response.Loading)
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userDocument = firestore.collection("users").document(currentUser.uid).get().await()
                val user = userDocument.toObject(User::class.java) ?: throw Exception("User not found")
                user.id = currentUser.uid
                emit(Response.Success(user))
            } else {
                emit(Response.Error("No user logged in"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
//            emit(Response.Error(e.message ?: "An error occurred"))
        }
    }

    override fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }
}