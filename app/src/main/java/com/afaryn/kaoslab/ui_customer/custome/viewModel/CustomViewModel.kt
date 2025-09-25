// CustomViewModel.kt
package com.afaryn.kaoslab.ui_customer.custome.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.model.CustomProduct
import com.afaryn.kaoslab.model.Product
import com.afaryn.kaoslab.utils.Constants.CUSTOM_PRODUCT_COLLECTION
import com.afaryn.kaoslab.utils.Constants.PRODUCT_COLLECTION
import com.afaryn.kaoslab.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CustomViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    private val _productState = MutableLiveData<UiState<List<CustomProduct>>>()
    val productState: LiveData<UiState<List<CustomProduct>>> = _productState

    private var _selectedProduct: CustomProduct? = null
    val selectedProduct: CustomProduct? get() = _selectedProduct

    // Tetap Set<String> karena kita akan membuat OrderItem per setiap label ukuran
    private val _selectedSizes = MutableLiveData<MutableSet<String>>()
    val selectedSizes: LiveData<MutableSet<String>> = _selectedSizes

    private val _selectedColor = MutableLiveData<String?>()
    val selectedColor: LiveData<String?> = _selectedColor

    var selectedCustomDesignUri: Uri? = null

    var selectedYourDesignUrl: String? = null

    private val _designsState = MutableLiveData<UiState<List<String>>>()
    val designsState: LiveData<UiState<List<String>>> get() = _designsState


    fun setSelectedProduct(product: CustomProduct) {
        _selectedProduct = product
        Log.d("CustomViewModel", "Produk dipilih: ${product.name}")
        _selectedSizes.value = mutableSetOf()
        _selectedColor.value = null
    }

    // Fungsi untuk mengatur ukuran yang dipilih dari StepTwo
    fun updateSelectedSizes(sizes: Set<String>) {
        _selectedSizes.value = sizes.toMutableSet()
        Log.d("CustomViewModel", "Ukuran dipilih: ${sizes.joinToString()}")
    }

    // Fungsi untuk mengatur warna yang dipilih dari StepTwo
    fun updateSelectedColor(color: String?) {
        _selectedColor.value = color
        Log.d("CustomViewModel", "Warna dipilih: $color")
    }

    // Fungsi untuk mengatur data kustomisasi (dari StepThree)
    fun setCustomDesign(uri: Uri?) {
        selectedCustomDesignUri = uri
        selectedYourDesignUrl = null
        Log.d("CustomViewModel", "Gambar kustom diatur: $uri")
    }

    fun setSelectedYourDesign(url: String?) {
        selectedYourDesignUrl = url
        selectedCustomDesignUri = null
        Log.d("CustomViewModel", "Desain Anda dipilih: $url")
    }

    // ----------------------------
    // Fetch Produk dari Firestore
    // ----------------------------
    fun fetchTopProducts() {
        fetchProductsByType("0")
    }

    fun fetchBottomProducts() {
        fetchProductsByType("1")
    }

    fun fetchHatProducts() {
        fetchProductsByType("2")
    }

    private fun fetchProductsByType(type: String) {
        _productState.value = UiState.Loading(true)

        firestore.collection(CUSTOM_PRODUCT_COLLECTION)
            .whereEqualTo("type", type)
            .get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.toObjects(CustomProduct::class.java)

                if (products.isNotEmpty()) {
                    _productState.value = UiState.Success(products)
                } else {
                    _productState.value = UiState.Error("Tidak ada produk ditemukan.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("CustomViewModel", "fetchProductsByType error", e)
                _productState.value = UiState.Error("Gagal Mengambil Data: ${e.message}")
            }
    }

    // ----------------------------
    // Fetch desain user (StepThree)
    // ----------------------------
    fun fetchUserDesigns() {
        _designsState.value = UiState.Loading(true)

        firestore.collection(PRODUCT_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                _designsState.value = UiState.Loading(false)
                val products = result.toObjects(Product::class.java)
                val designs = products.mapNotNull { it.imageUrl }

                if (designs.isNotEmpty()) {
                    _designsState.value = UiState.Success(designs)
                } else {
                    _designsState.value = UiState.Error("Tidak ada desain ditemukan.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("CustomViewModel", "fetchUserDesigns error", e)
                _designsState.value = UiState.Loading(false)
                _designsState.value = UiState.Error(e.message ?: "Unknown error")
            }
    }
}
