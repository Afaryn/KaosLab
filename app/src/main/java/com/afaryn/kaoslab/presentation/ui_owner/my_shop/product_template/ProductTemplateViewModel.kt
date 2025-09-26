package com.afaryn.kaoslab.presentation.ui_owner.my_shop.product_template

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.repository.OwnerRepository
import com.afaryn.kaoslab.domain.model.ProductTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductTemplateViewModel @Inject constructor(
    private val ownerRepository: OwnerRepository
) : ViewModel() {

    private val _productTemplates = MutableStateFlow<List<ProductTemplate>>(emptyList())
    val productTemplates: StateFlow<List<ProductTemplate>> = _productTemplates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadProductTemplates()
    }

    private fun loadProductTemplates() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                ownerRepository.getProductTemplates().collect { templates ->
                    _productTemplates.value = templates
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun getTemplatesByType(type: String): List<ProductTemplate> {
        return _productTemplates.value.filter { it.type == type }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun addNewTemplate() {
        // This can be implemented later for adding new templates
        // For now, it's just a placeholder for the "Add new template" button
    }
}
