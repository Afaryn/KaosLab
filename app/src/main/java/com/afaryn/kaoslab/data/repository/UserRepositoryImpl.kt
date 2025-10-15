package com.afaryn.kaoslab.data.repository

import android.net.Uri
import androidx.core.net.toUri
import com.afaryn.kaoslab.data.remote.MidtransApi
import com.afaryn.kaoslab.domain.model.Address
import com.afaryn.kaoslab.domain.model.CartProduct
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.domain.model.DesignUplType
import com.afaryn.kaoslab.domain.model.Feed
import com.afaryn.kaoslab.domain.model.Likes
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.domain.model.OrderStatus
import com.afaryn.kaoslab.domain.model.SnapRequest
import com.afaryn.kaoslab.domain.model.SnapResponse
import com.afaryn.kaoslab.domain.model.User
import com.afaryn.kaoslab.domain.repository.UserRepository
import com.afaryn.kaoslab.utils.Constants.COLL_ADDRESS
import com.afaryn.kaoslab.utils.Constants.COLL_CART
import com.afaryn.kaoslab.utils.Constants.COLL_FEED
import com.afaryn.kaoslab.utils.Constants.COLL_ORDERS
import com.afaryn.kaoslab.utils.Constants.COLL_USER
import com.afaryn.kaoslab.utils.Constants.COLL_USER_DESIGN
import com.afaryn.kaoslab.utils.Constants.COLL_USER_DESIGN_PENDING
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_PENDING
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_SETTLEMENT
import com.afaryn.kaoslab.utils.PaymentConstants.STATUS_SUCCESS
import com.afaryn.kaoslab.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val midtransApi: MidtransApi
) : UserRepository {
    override fun getUserId(): String {
        return auth.currentUser?.uid.orEmpty()
    }

    override fun addToCart(cartProduct: CartProduct): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.currentUser?.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        try {
            val designType = cartProduct.orderItem?.designType
            val url = designType?.takeIf { it.type == DesignUplType.Upload.value }?.overlay?.toUri()
                ?.let { uri ->
                    val ref = storage.reference.child("uploads/custom_d_${UUID.randomUUID()}.jpg")
                    ref.putFile(uri).await()
                    ref.downloadUrl.await().toString()
                } ?: designType?.overlay

            firestore.collection(COLL_USER)
                .document(uid)
                .collection(COLL_CART)
                .document(cartProduct.id)
                .set(
                    cartProduct.copy(
                        orderItem = cartProduct.orderItem?.copy(
                            designType = designType?.copy(overlay = url)
                        )
                    )
                ).await()

            trySend(Resource.Success(Unit))
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }

        awaitClose { }
    }

    override fun getCartProducts(): Flow<Resource<List<CartProduct>>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(COLL_USER).document(uid).collection(COLL_CART)
            .addSnapshotListener { value, error ->
                error?.let {
                    trySend(Resource.Error(it.message ?: "Terjadi kesalahan"))
                    close()
                    return@addSnapshotListener
                }

                value?.toObjects(CartProduct::class.java)?.let {
                    trySend(Resource.Success(it))
                }
            }

        awaitClose { listener.remove() }
    }

    override fun deleteCartProduct(id: String): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.currentUser?.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        try {
            firestore.collection(COLL_USER)
                .document(uid)
                .collection(COLL_CART)
                .document(id)
                .delete().await()

            trySend(Resource.Success(Unit))
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }

        awaitClose { }
    }

    override fun getLastAddress(): Flow<Resource<Address>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        try {
            val address = firestore.collection(COLL_USER)
                .document(uid)
                .collection(COLL_ADDRESS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get().await().toObjects(Address::class.java)

            address.firstOrNull()?.let { trySend(Resource.Success(it)) }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }

        awaitClose { }
    }

    override fun getAddress(): Flow<Resource<List<Address>>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(COLL_USER).document(uid)
            .collection(COLL_ADDRESS)
            .addSnapshotListener { value, error ->
                error?.let {
                    trySend(Resource.Error(it.message ?: "Terjadi kesalahan"))
                    close()
                    return@addSnapshotListener
                }

                value?.toObjects(Address::class.java)?.let {
                    trySend(Resource.Success(it))
                }
            }

        awaitClose { listener.remove() }
    }

    override fun createAddress(address: Address): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        try {
            firestore.collection(COLL_USER)
                .document(uid)
                .collection(COLL_ADDRESS)
                .document(address.id)
                .set(address).await()

            trySend(Resource.Success(Unit))
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }

        awaitClose { }
    }

    override fun deleteAddress(id: String): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        try {
            firestore.collection(COLL_USER)
                .document(uid)
                .collection(COLL_ADDRESS)
                .document(id)
                .delete().await()

            trySend(Resource.Success(Unit))
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Terjadi kesalahan"))
        }

        awaitClose { }
    }

    override suspend fun getSnapToken(
        order: Order?,
        design: Design?
    ): Flow<Resource<SnapResponse>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        try {
            val user = firestore.collection(COLL_USER)
                .document(uid).get().await().toObject(User::class.java)
                ?: throw Exception("Gagal mendapatkan data user")

            val (name, email) = (user.name
                ?: throw Exception("Failed getting user's name")) to (user.email
                ?: throw Exception("Failed getting user's email"))

            val request = order?.let {
                SnapRequest(
                    orderId = order.orderId,
                    amount = (order.totalAmount + 7000).toLong(),
                    name = name,
                    email = email
                )
            } ?: design?.let {
                SnapRequest(
                    orderId = design.id,
                    amount = design.selectedLicense?.price?.toLong()
                        ?: throw Exception("Failed getting selected license"),
                    name = name,
                    email = email
                )
            } ?: throw Exception("Failed getting data")

            val response = midtransApi.createSnap(request)

            trySend(Resource.Success(response))
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "There is trouble getting data"))
        }

        awaitClose { }
    }

    override fun clearCart(order: Order, snapToken: String?): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        try {
            val cartIds = order.cartProducts.map { it.id }
            val batch = firestore.batch()

            val cartCollection = firestore.collection(COLL_USER)
                .document(uid)
                .collection(COLL_CART)

            val ordersCollection = firestore.collection(COLL_USER)
                .document(uid)
                .collection(COLL_ORDERS)

            val orderRef = ordersCollection.document(order.orderId)
            batch.set(orderRef, order.copy(snapToken = snapToken))

            cartIds.forEach { cartId ->
                val docRef = cartCollection.document(cartId)
                batch.delete(docRef)
            }

            batch.commit().await()
            trySend(Resource.Success(Unit))
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "There is trouble getting data"))
        }

        awaitClose { }
    }

    override fun addDesign(design: Design, isPending: Boolean): Flow<Resource<Unit>> =
        callbackFlow {
            trySend(Resource.Loading)

            val uid = auth.uid ?: run {
                trySend(Resource.Error("Gagal mendapatkan data user"))
                close()
                return@callbackFlow
            }

            try {
                val coll = if (isPending) COLL_USER_DESIGN_PENDING else COLL_USER_DESIGN

                firestore.collection(COLL_USER)
                    .document(uid)
                    .collection(coll)
                    .document(design.id)
                    .set(design)
                    .await()

                trySend(Resource.Success(Unit))
            } catch (e: Exception) {
                trySend(Resource.Error(e.message ?: "There is trouble getting data"))
            }

            awaitClose { }
        }

    override fun getOwnedDesigns(isPending: Boolean): Flow<Resource<List<Design>>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        val coll = if (isPending) COLL_USER_DESIGN_PENDING else COLL_USER_DESIGN

        val listener = firestore.collection(COLL_USER).document(uid)
            .collection(coll)
            .addSnapshotListener { value, error ->
                error?.let {
                    trySend(Resource.Error(it.message ?: "Terjadi kesalahan"))
                    close()
                    return@addSnapshotListener
                }

                value?.toObjects(Design::class.java)?.let {
                    trySend(Resource.Success(it))
                }
            }

        awaitClose { listener.remove() }
    }

    override fun postFeed(feed: Feed, imgUri: Uri): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        try {
            val fileRef = storage.reference.child("feeds/${feed.id}.jpg")
            fileRef.putFile(imgUri).await()
            val imgUrl = fileRef.downloadUrl.await().toString()

            val user = firestore.collection(COLL_USER).document(uid).get().await()
                .toObject(User::class.java) ?: throw Exception("Failed getting user data")

            firestore.collection(COLL_FEED)
                .document(feed.id)
                .set(feed.copy(userId = uid, user = user, photoUrl = imgUrl))
                .await()

            trySend(Resource.Success(Unit))
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "There is trouble getting data"))
        }

        awaitClose { }
    }

    override fun getFeeds(): Flow<Resource<List<Feed>>> = callbackFlow {
        trySend(Resource.Loading)

        val listener = firestore.collection(COLL_FEED)
            .addSnapshotListener { value, error ->
                error?.let {
                    trySend(Resource.Error(it.message ?: "Terjadi kesalahan"))
                    close()
                    return@addSnapshotListener
                }

                value?.toObjects(Feed::class.java)?.let {
                    trySend(Resource.Success(it))
                }
            }

        awaitClose { listener.remove() }
    }

    override fun likeFeed(feedId: String, isLiking: Boolean): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        try {
            val feed = firestore.collection(COLL_FEED).document(feedId).get().await()
                .toObject(Feed::class.java) ?: throw Exception("Failed getting feed data")

            val likes = feed.likes.toMutableList()
            if (isLiking) likes.add(Likes(userId = uid))
            else likes.remove(likes.find { it.userId == uid })

            firestore.collection(COLL_FEED)
                .document(feed.id)
                .set(feed.copy(likes = likes))
                .await()

            trySend(Resource.Success(Unit))
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "There is trouble getting data"))
        }

        awaitClose { }
    }

    override fun getOrders(status: String): Flow<Resource<List<Order>>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(COLL_USER)
            .document(uid)
            .collection(COLL_ORDERS)
            .whereEqualTo("status", status)
            .addSnapshotListener { value, error ->
                error?.let {
                    trySend(Resource.Error(it.message ?: "Terjadi kesalahan"))
                    close()
                    return@addSnapshotListener
                }

                value?.toObjects(Order::class.java)?.let {
                    trySend(Resource.Success(it))
                }
            }

        awaitClose { listener.remove() }
    }

    override fun updatePaymentStatus(order: Order): Flow<Resource<Pair<String, Boolean>>> = callbackFlow {
        trySend(Resource.Loading)

        val uid = auth.uid ?: run {
            trySend(Resource.Error("Gagal mendapatkan data user"))
            close()
            return@callbackFlow
        }

        try {
            val response = midtransApi.getStatus(order.orderId)

            when (response.transactionStatus) {
                in listOf(STATUS_SUCCESS, STATUS_SETTLEMENT) -> {
                    firestore.collection(COLL_USER)
                        .document(uid)
                        .collection(COLL_ORDERS)
                        .document(order.orderId)
                        .set(order.copy(status = OrderStatus.Processing.value))
                        .await()

                    trySend(Resource.Success("Payment Successful" to true))
                    close()
                    return@callbackFlow
                }

                STATUS_PENDING -> {
                    trySend(Resource.Success(order.snapToken.orEmpty() to false))
                    close()
                    return@callbackFlow
                }

                else -> {
                    trySend(Resource.Error("Payment failed or unknown"))
                    close()
                    return@callbackFlow
                }
            }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "There is trouble getting data"))
        }

        awaitClose { }
    }
}
