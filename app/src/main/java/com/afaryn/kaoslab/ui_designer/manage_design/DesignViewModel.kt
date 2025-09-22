package com.afaryn.kaoslab.ui_designer.manage_design

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.data.DesignerRepository
import com.afaryn.kaoslab.model.Design
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DesignViewModel @Inject constructor(
    private val repository: DesignerRepository
) : ViewModel() {

    private val _designsState = MutableStateFlow<Response<List<Design>>>(Response.Idle)
    val designsState: StateFlow<Response<List<Design>>> = _designsState.asStateFlow()

    private val _addDesignState = MutableStateFlow<Response<String>>(Response.Idle)
    val addDesignState: StateFlow<Response<String>> = _addDesignState.asStateFlow()

    private val _editDesignState = MutableStateFlow<Response<String>>(Response.Idle)
    val editDesignState: StateFlow<Response<String>> = _editDesignState.asStateFlow()

    private val _deleteDesignState = MutableStateFlow<Response<String>>(Response.Idle)
    val deleteDesignState: StateFlow<Response<String>> = _deleteDesignState.asStateFlow()

    private val _singleDesignState = MutableStateFlow<Response<Design>>(Response.Idle)
    val singleDesignState: StateFlow<Response<Design>> = _singleDesignState.asStateFlow()

    fun getDesigns() {
        viewModelScope.launch {
            repository.getDesigns().collect { response ->
                _designsState.value = response
            }
        }
    }

    fun addDesign(design: Design, imageUri: Uri? = null) {
        viewModelScope.launch {
            try {
                _addDesignState.value = Response.Loading

                // Generate design ID for Firebase Storage naming
                val designId = java.util.UUID.randomUUID().toString()

                val imageUrl = if (imageUri != null) {
                    // Upload image to Firebase Storage
                    repository.uploadDesignImage(imageUri, designId)
                } else {
                    design.fileUrl // Keep existing URL if no new image
                }

                val designWithImage = design.copy(
                    id = designId,
                    fileUrl = imageUrl
                )

                repository.addDesign(designWithImage).collect { response ->
                    _addDesignState.value = response
                    if (response is Response.Success) {
                        getDesigns() // Refresh the list
                    }
                }
            } catch (e: Exception) {
                _addDesignState.value = Response.Error(e.message ?: "Failed to add design")
            }
        }
    }

    fun updateDesign(design: Design, newImageUri: Uri? = null) {
        viewModelScope.launch {
            try {
                _editDesignState.value = Response.Loading

                val updatedDesign = if (newImageUri != null) {
                    // Delete old image if exists
                    if (design.fileUrl.isNotEmpty()) {
                        repository.deleteImageFromStorage(design.fileUrl)
                    }

                    // Upload new image
                    val newImageUrl = repository.uploadDesignImage(newImageUri, design.id)
                    design.copy(fileUrl = newImageUrl)
                } else {
                    design
                }

                repository.updateDesign(updatedDesign).collect { response ->
                    _editDesignState.value = response
                    if (response is Response.Success) {
                        getDesigns() // Refresh the list
                    }
                }
            } catch (e: Exception) {
                _editDesignState.value = Response.Error(e.message ?: "Failed to update design")
            }
        }
    }

    fun deleteDesign(design: Design) {
        viewModelScope.launch {
            try {
                _deleteDesignState.value = Response.Loading

                // Delete image from storage first
                if (design.fileUrl.isNotEmpty()) {
                    repository.deleteImageFromStorage(design.fileUrl)
                }

                // Then delete design document
                repository.deleteDesign(design.id).collect { response ->
                    _deleteDesignState.value = response
                    if (response is Response.Success) {
                        getDesigns() // Refresh the list
                    }
                }
            } catch (e: Exception) {
                _deleteDesignState.value = Response.Error(e.message ?: "Failed to delete design")
            }
        }
    }

    fun getDesignById(designId: String) {
        viewModelScope.launch {
            repository.getDesignById(designId).collect { response ->
                _singleDesignState.value = response
            }
        }
    }

    fun resetAddDesignState() {
        _addDesignState.value = Response.Idle
    }

    fun resetEditDesignState() {
        _editDesignState.value = Response.Idle
    }

    fun resetDeleteDesignState() {
        _deleteDesignState.value = Response.Idle
    }

    fun resetSingleDesignState() {
        _singleDesignState.value = Response.Idle
    }
}
