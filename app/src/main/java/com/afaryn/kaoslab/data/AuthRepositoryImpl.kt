package com.afaryn.kaoslab.data

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
                val userId = result.user?.uid ?: throw Exception("User ID is null")
                val userDocument = firestore.collection("users").document(userId).get().await()
                val user = userDocument.toObject(User::class.java) ?: throw Exception("User not found")
                user.id = userId
                emit(Response.Success(user))
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
                emit(Response.Success("User registered successfully"))
            } catch (e: Exception) {
                emit(Response.Error(e.message ?: "An error occurred"))
            }
        }

    override fun logout() {
        auth.signOut()
    }

    override fun userUid(): String = auth.currentUser?.uid ?: ""

    override fun isLoggedIn(): Flow<Boolean> = flow {
        emit(auth.currentUser != null)
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
            emit(Response.Error(e.message ?: "An error occurred"))
        }
    }
}