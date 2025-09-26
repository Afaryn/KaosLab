package com.afaryn.kaoslab.presentation.ui_owner.my_shop.add_template

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaryn.kaoslab.domain.repository.OwnerRepository
import com.afaryn.kaoslab.domain.model.ProductTemplate
import com.afaryn.kaoslab.utils.Response
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTemplateViewModel @Inject constructor(
    private val ownerRepository: OwnerRepository,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _uploadState = MutableStateFlow<Response<String>>(Response.Idle)
    val uploadState: StateFlow<Response<String>> = _uploadState.asStateFlow()

    fun uploadTemplate(template: ProductTemplate, imageUri: Uri) {
        viewModelScope.launch {
            try {
                _uploadState.value = Response.Loading
                val imageUrl = uploadImage(imageUri)
                val templateWithImage = template.copy(imageUrl = imageUrl)
                ownerRepository.addProductTemplate(templateWithImage).collect { response ->
                    _uploadState.value = response
                }
            } catch (e: Exception) {
                _uploadState.value = Response.Error(e.message ?: "Upload failed")
            }
        }
    }

    private suspend fun uploadImage(imageUri: Uri): String {
        val imageRef = storage.reference
            .child("custom_product")
            .child("${UUID.randomUUID()}.jpg")

        val uploadTask = imageRef.putFile(imageUri).await()
        return imageRef.downloadUrl.await().toString()
    }

    fun resetState() {
        _uploadState.value = Response.Idle
    }
}
