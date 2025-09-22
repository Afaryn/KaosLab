package com.afaryn.kaoslab.ui_designer.edit_profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.data.DesignerRepository
import com.afaryn.kaoslab.model.User
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: DesignerRepository
) : ViewModel() {

    private val _userProfileState = MutableStateFlow<Response<User>>(Response.Idle)
    val userProfileState: StateFlow<Response<User>> = _userProfileState.asStateFlow()

    private val _updateProfileState = MutableStateFlow<Response<String>>(Response.Idle)
    val updateProfileState: StateFlow<Response<String>> = _updateProfileState.asStateFlow()

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
