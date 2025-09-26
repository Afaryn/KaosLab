package com.afaryn.kaoslab.presentation.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.kaoslab.domain.repository.AuthRepository
import com.afaryn.kaoslab.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository
) : ViewModel() {
    fun login(email: String, password: String) = repository.login(email, password).asLiveData()

    fun register(email: String, password: String, user: User) =
        repository.register(email, password, user).asLiveData()

    fun logout() = repository.logout()
    fun userUid(): String = repository.userUid()
    fun getCurrentUser() = repository.getCurrentUser().asLiveData()
    fun isUserLoggedIn() = repository.isLoggedIn().asLiveData()
}