package com.afaryn.kaoslab.ui_designer.manage_portofolio

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.data.DesignerRepository
import com.afaryn.kaoslab.model.Portfolio
import com.afaryn.kaoslab.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val repository: DesignerRepository
) : ViewModel() {

    private val _portfoliosState = MutableStateFlow<Response<List<Portfolio>>>(Response.Idle)
    val portfoliosState: StateFlow<Response<List<Portfolio>>> = _portfoliosState.asStateFlow()

    private val _addPortfolioState = MutableStateFlow<Response<String>>(Response.Idle)
    val addPortfolioState: StateFlow<Response<String>> = _addPortfolioState.asStateFlow()

    private val _updatePortfolioState = MutableStateFlow<Response<String>>(Response.Idle)
    val updatePortfolioState: StateFlow<Response<String>> = _updatePortfolioState.asStateFlow()

    private val _deletePortfolioState = MutableStateFlow<Response<String>>(Response.Idle)
    val deletePortfolioState: StateFlow<Response<String>> = _deletePortfolioState.asStateFlow()

    private val _portfolioDetailState = MutableStateFlow<Response<Portfolio>>(Response.Idle)
    val portfolioDetailState: StateFlow<Response<Portfolio>> = _portfolioDetailState.asStateFlow()

    fun getPortfolios() {
        viewModelScope.launch {
            repository.getPortfolios().collect { response ->
                _portfoliosState.value = response
            }
        }
    }

    fun addPortfolio(title: String, description: String, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                _addPortfolioState.value = Response.Loading

                // First, generate portfolio ID for Firebase Storage naming
                val portfolioId = java.util.UUID.randomUUID().toString()

                val imageUrl = if (imageUri != null) {
                    // Upload image to Firebase Storage
                    repository.uploadPortfolioImage(imageUri, portfolioId)
                } else {
                    ""
                }

                val portfolio = Portfolio(
                    id = portfolioId,
                    title = title,
                    description = description,
                    imageUrl = imageUrl
                )

                repository.addPortfolio(portfolio).collect { response ->
                    _addPortfolioState.value = response
                }
            } catch (e: Exception) {
                _addPortfolioState.value = Response.Error(e.message ?: "Failed to add portfolio")
            }
        }
    }

    fun updatePortfolio(portfolio: Portfolio, newImageUri: Uri? = null) {
        viewModelScope.launch {
            try {
                _updatePortfolioState.value = Response.Loading

                val updatedPortfolio = if (newImageUri != null) {
                    // Delete old image if exists
                    if (portfolio.imageUrl.isNotEmpty()) {
                        repository.deleteImageFromStorage(portfolio.imageUrl)
                    }

                    // Upload new image
                    val newImageUrl = repository.uploadPortfolioImage(newImageUri, portfolio.id)
                    portfolio.copy(imageUrl = newImageUrl)
                } else {
                    portfolio
                }

                repository.updatePortfolio(updatedPortfolio).collect { response ->
                    _updatePortfolioState.value = response
                }
            } catch (e: Exception) {
                _updatePortfolioState.value = Response.Error(e.message ?: "Failed to update portfolio")
            }
        }
    }

    fun deletePortfolio(portfolio: Portfolio) {
        viewModelScope.launch {
            try {
                _deletePortfolioState.value = Response.Loading

                // Delete image from storage first
                if (portfolio.imageUrl.isNotEmpty()) {
                    repository.deleteImageFromStorage(portfolio.imageUrl)
                }

                // Then delete portfolio document
                repository.deletePortfolio(portfolio.id).collect { response ->
                    _deletePortfolioState.value = response
                }
            } catch (e: Exception) {
                _deletePortfolioState.value = Response.Error(e.message ?: "Failed to delete portfolio")
            }
        }
    }

    fun getPortfolioById(portfolioId: String) {
        viewModelScope.launch {
            repository.getPortfolioById(portfolioId).collect { response ->
                _portfolioDetailState.value = response
            }
        }
    }

    fun resetAddPortfolioState() {
        _addPortfolioState.value = Response.Idle
    }

    fun resetUpdatePortfolioState() {
        _updatePortfolioState.value = Response.Idle
    }

    fun resetDeletePortfolioState() {
        _deletePortfolioState.value = Response.Idle
    }
}
