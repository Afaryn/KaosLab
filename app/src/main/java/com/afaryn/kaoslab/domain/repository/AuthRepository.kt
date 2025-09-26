package com.afaryn.kaoslab.domain.repository

import com.afaryn.kaoslab.domain.model.User
import com.afaryn.kaoslab.utils.Response
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Response<User>>
    fun register(email: String, password: String, user: User): Flow<Response<String>>
    fun logout()
    fun userUid(): String
    fun isLoggedIn(): Flow<Boolean>
    fun getCurrentUser(): Flow<Response<User>>
}