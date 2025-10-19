package com.afaryn.kaoslab.presentation.ui_customer.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.utils.Constants.DESIGN_COLLECTION
import com.afaryn.kaoslab.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    fun getProduct() = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.currentUser?.uid ?: run {
            trySend(Resource.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(DESIGN_COLLECTION)
            .whereNotEqualTo("designerId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("HomeViewModel", "Error fetching products: ${error.message}")
                    trySend(Resource.Error(error.message ?: "Unknown error occurred"))
                    close()
                    return@addSnapshotListener
                }

                trySend(Resource.Success(snapshot?.toObjects(Design::class.java).orEmpty()))
            }

        awaitClose { listener.remove() }
    }
}
