package com.afaryn.kaoslab.domain.repository

import android.net.Uri
import com.afaryn.kaoslab.domain.model.Address
import com.afaryn.kaoslab.domain.model.CartProduct
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.domain.model.Feed
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.domain.model.SnapResponse
import com.afaryn.kaoslab.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // User
    fun getUserId(): String

    // Cart
    fun addToCart(cartProduct: CartProduct): Flow<Resource<Unit>>
    fun getCartProducts(): Flow<Resource<List<CartProduct>>>
    fun deleteCartProduct(id: String): Flow<Resource<Unit>>

    // Address
    fun getLastAddress(): Flow<Resource<Address>>
    fun getAddress(): Flow<Resource<List<Address>>>
    fun createAddress(address: Address): Flow<Resource<Unit>>
    fun deleteAddress(id: String): Flow<Resource<Unit>>

    // Payment
    suspend fun getSnapToken(order: Order? = null, design: Design? = null): Flow<Resource<SnapResponse>>
    fun clearCart(order: Order, snapToken: String?): Flow<Resource<Unit>>

    // Design
    fun addDesign(design: Design, isPending: Boolean): Flow<Resource<Unit>>
    fun getOwnedDesigns(isPending: Boolean): Flow<Resource<List<Design>>>

    // Feed
    fun postFeed(feed: Feed, imgUri: Uri): Flow<Resource<Unit>>
    fun getFeeds(): Flow<Resource<List<Feed>>>
    fun likeFeed(feedId: String, isLiking: Boolean): Flow<Resource<Unit>>

    // Orders
    fun getOrders(status: String): Flow<Resource<List<Order>>>
    fun updatePaymentStatus(order: Order): Flow<Resource<Pair<String, Boolean>>>
}