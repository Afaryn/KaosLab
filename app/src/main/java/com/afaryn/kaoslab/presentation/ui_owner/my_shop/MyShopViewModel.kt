package com.afaryn.kaoslab.presentation.ui_owner.my_shop

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.model.User
import com.afaryn.kaoslab.domain.repository.AuthRepository
import com.afaryn.kaoslab.domain.repository.OwnerRepository
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyShopViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: OwnerRepository
) : ViewModel() {

    private val _userProfileState = MutableStateFlow<Response<User>>(Response.Idle)
    val userProfileState: StateFlow<Response<User>> = _userProfileState.asStateFlow()

    private val _updateProfileState = MutableStateFlow<Response<String>>(Response.Idle)
    val updateProfileState: StateFlow<Response<String>> = _updateProfileState.asStateFlow()

    fun logOut() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun getCurrentUser() = authRepository.getCurrentUser().asLiveData()

    fun getUserProfile() {
        viewModelScope.launch {
            repository.getCurrentUser().collect { response ->
                _userProfileState.value = response
            }
        }
    }

    fun updateProfile(user: User, newImageUri: Uri? = null) {
        viewModelScope.launch {
            try {
                _updateProfileState.value = Response.Loading

                val updatedUser = if (newImageUri != null) {
                    // Delete old profile image if exists
                    if (user.profilePicture.isNotEmpty()) {
                        repository.deleteImageFromStorage(user.profilePicture)
                    }

                    // Upload new profile image
                    val newImageUrl = repository.uploadProfileImage(newImageUri, user.id)
                    user.copy(profilePicture = newImageUrl)
                } else {
                    user
                }

                repository.updateUserProfile(updatedUser).collect { response ->
                    _updateProfileState.value = response
                    if (response is Response.Success) {
                        // Refresh user profile data
                        getUserProfile()
                    }
                }
            } catch (e: Exception) {
                _updateProfileState.value = Response.Error(e.message ?: "Failed to update profile")
            }
        }
    }

    fun resetUpdateProfileState() {
        _updateProfileState.value = Response.Idle
    }
}