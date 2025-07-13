package com.afaryn.kaoslab.ui_customer.custome.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.model.CustomProduct
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.afaryn.kaoslab.utils.Constants.CUSTOM_PRODUCT_COLLECTION
import com.afaryn.kaoslab.utils.UiState

@HiltViewModel
class CustomViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    private val _productState = MutableLiveData<UiState<List<CustomProduct>>>()
    val productState: LiveData<UiState<List<CustomProduct>>> = _productState

    private var _selectedProduct: CustomProduct? = null
    val selectedProduct: CustomProduct? get() = _selectedProduct

    var selectedSizes: MutableSet<String> = mutableSetOf()
    var selectedColor: String? = null


    fun setSelectedProduct(product: CustomProduct) {
        _selectedProduct = product
        Log.d("SELECTED_PRODUCT", "Produk dipilih: ${product.name}")
    }

    fun fetchTopProducts() {
        _productState.value = UiState.Loading(true)

        firestore.collection(CUSTOM_PRODUCT_COLLECTION)
            .whereEqualTo("type", "0")
            .get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.toObjects(CustomProduct::class.java)

                if (products.isNotEmpty()) {
                    _productState.value = UiState.Success(products)
                    Log.d("CUSTOM_PRODUCTS", "Berhasil ambil data: ${products.size} item")
                    products.forEach { Log.d("CUSTOM_PRODUCT_ITEM", it.toString()) }
                } else {
                    _productState.value = UiState.Error("Tidak ada produk ditemukan.")
                    Log.w("CUSTOM_PRODUCTS", "Data kosong.")
                }
            }
            .addOnFailureListener { e ->
                _productState.value = UiState.Error("Gagal Mengambil Data: ${e.message}")
                Log.e("CUSTOM_PRODUCTS", "Error ambil data", e)
            }
    }

    fun fetchBottomProducts() {
        _productState.value = UiState.Loading(true)

        firestore.collection(CUSTOM_PRODUCT_COLLECTION)
            .whereEqualTo("type", "1")
            .get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.toObjects(CustomProduct::class.java)

                if (products.isNotEmpty()) {
                    _productState.value = UiState.Success(products)
                    Log.d("CUSTOM_PRODUCTS", "Berhasil ambil data: ${products.size} item")
                    products.forEach { Log.d("CUSTOM_PRODUCT_ITEM", it.toString()) }
                } else {
                    _productState.value = UiState.Error("Tidak ada produk ditemukan.")
                    Log.w("CUSTOM_PRODUCTS", "Data kosong.")
                }
            }
            .addOnFailureListener { e ->
                _productState.value = UiState.Error("Gagal Mengambil Data: ${e.message}")
                Log.e("CUSTOM_PRODUCTS", "Error ambil data", e)
            }
    }

    fun fetchHatProducts() {
        _productState.value = UiState.Loading(true)

        firestore.collection(CUSTOM_PRODUCT_COLLECTION)
            .whereEqualTo("type", "2")
            .get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.toObjects(CustomProduct::class.java)

                if (products.isNotEmpty()) {
                    _productState.value = UiState.Success(products)
                    Log.d("CUSTOM_PRODUCTS", "Berhasil ambil data: ${products.size} item")
                    products.forEach { Log.d("CUSTOM_PRODUCT_ITEM", it.toString()) }
                } else {
                    _productState.value = UiState.Error("Tidak ada produk ditemukan.")
                    Log.w("CUSTOM_PRODUCTS", "Data kosong.")
                }
            }
            .addOnFailureListener { e ->
                _productState.value = UiState.Error("Gagal Mengambil Data: ${e.message}")
                Log.e("CUSTOM_PRODUCTS", "Error ambil data", e)
            }
    }


}
