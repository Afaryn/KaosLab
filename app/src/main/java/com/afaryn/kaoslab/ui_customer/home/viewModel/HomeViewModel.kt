package com.afaryn.kaoslab.ui_customer.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.afaryn.kaoslab.model.Product
import com.afaryn.kaoslab.utils.Constants.PRODUCT_COLLECTION
import com.afaryn.kaoslab.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _product = MutableStateFlow<UiState<List<Product>>>(UiState.Loading(false))
    val product = _product.asStateFlow().asLiveData()

    init {
        getProduct()
    }

    private fun getProduct() {
        _product.value = UiState.Loading(true)
        firestore.collection(PRODUCT_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("HomeViewModel", "Error fetching products: ${error.message}")
                    _product.value = UiState.Loading(false)
                    _product.value = UiState.Error(error.message ?: "Unknown error occurred")
                    return@addSnapshotListener
                }

                val productList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)
                }

                _product.value = UiState.Loading(false)

                if (!productList.isNullOrEmpty()) {
                    _product.value = UiState.Success(productList)
                    Log.d("HomeViewModel", "Fetched ${productList.size} products")
                } else {
                    _product.value = UiState.Error("No products found.")
                    Log.w("HomeViewModel", "Product list is empty or null")
                }
            }
    }
}
