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

    fun fetchProducts() {
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


//    fun submitMultipleProducts(products: List<CustomProduct>) {
//        val collection = firestore.collection(CUSTOM_PRODUCT_COLLECTION)
//
//        products.forEach { product ->
//            collection.add(product)
//                .addOnSuccessListener {
//                    Log.d("Firestore", "Produk ${product.name} berhasil ditambahkan")
//                }
//                .addOnFailureListener { e ->
//                    Log.e("Firestore", "Gagal tambah produk ${product.name}", e)
//                }
//        }
//    }

}

//@HiltViewModel
//class AddProductViewModel @Inject constructor(
//    private val firestore: FirebaseFirestore,
//    private val storage: FirebaseStorage
//) : ViewModel() {
//
//    fun submitProduct(product: Product) {
//        firestore.collection("products")
//            .add(product)
//            .addOnSuccessListener {
//                Log.d("Product", "Berhasil tambah produk")
//            }
//            .addOnFailureListener {
//                Log.e("Product", "Gagal tambah produk", it)
//            }
//    }
//
//    fun uploadImage(fileUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
//        val ref = storage.reference.child("products/${UUID.randomUUID()}.jpg")
//        ref.putFile(fileUri)
//            .continueWithTask { task ->
//                if (!task.isSuccessful) throw task.exception ?: Exception("Upload gagal")
//                ref.downloadUrl
//            }.addOnSuccessListener { uri ->
//                onSuccess(uri.toString())
//            }.addOnFailureListener {
//                onFailure(it)
//            }
//    }
//}
