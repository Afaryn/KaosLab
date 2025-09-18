package com.afaryn.kaoslab.ui_designer.manage_design

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

    fun addDesign(design: Design) {
        viewModelScope.launch {
            repository.addDesign(design).collect { response ->
                _addDesignState.value = response
                if (response is Response.Success) {
                    getDesigns() // Refresh the list
                }
            }
        }
    }

    fun deleteDesign(designId: String) {
        viewModelScope.launch {
            repository.deleteDesign(designId).collect { response ->
                _deleteDesignState.value = response
                if (response is Response.Success) {
                    getDesigns() // Refresh the list
                }
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

    fun updateDesign(design: Design) {
        viewModelScope.launch {
            repository.updateDesign(design).collect { response ->
                _editDesignState.value = response
                if (response is Response.Success) {
                    getDesigns() // Refresh the list
                }
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
