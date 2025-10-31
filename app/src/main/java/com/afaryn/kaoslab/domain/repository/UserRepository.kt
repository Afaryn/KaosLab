package com.afaryn.kaoslab.domain.repository

import com.afaryn.kaoslab.domain.model.Address
import com.afaryn.kaoslab.domain.model.CartProduct
import com.afaryn.kaoslab.domain.model.CustomProduct
import com.afaryn.kaoslab.domain.model.Design
import com.afaryn.kaoslab.domain.model.DesignOrder
import com.afaryn.kaoslab.domain.model.DesignOrderStatus
import com.afaryn.kaoslab.domain.model.Order
import com.afaryn.kaoslab.domain.model.Portfolio
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
    suspend fun getSnapToken(order: Order? = null, design: DesignOrder? = null): Flow<Resource<SnapResponse>>
    fun clearCart(order: Order, snapToken: String?): Flow<Resource<Unit>>

    // Design
    fun addDesign(design: DesignOrder): Flow<Resource<Unit>>
    fun getOwnedDesigns(status: DesignOrderStatus): Flow<Resource<List<DesignOrder>>>
    fun updateDesignPaymentStatus(order: DesignOrder): Flow<Resource<Pair<String, Boolean>>>
    fun downloadDesign(order: DesignOrder): Flow<Resource<Unit>>
    fun modifyFavorite(design: Design): Flow<Resource<Unit>>
    fun checkFavorite(designId: String): Flow<Resource<Boolean>>
    fun getFavorites(): Flow<Resource<List<Design>>>
    fun rateDesign(designOrder: DesignOrder, designId: String, rating: Float): Flow<Resource<Unit>>

    // Feed
//    fun postFeed(feed: Feed, imgUri: Uri): Flow<Resource<Unit>>
    fun getFeeds(): Flow<Resource<List<Portfolio>>>
    fun likeFeed(feedId: String, isLiking: Boolean): Flow<Resource<Unit>>

    // Orders
    fun getOrders(status: String): Flow<Resource<List<Order>>>
    fun updatePaymentStatus(order: Order): Flow<Resource<Pair<String, Boolean>>>

    // Account
    fun getOwnerContact(): Flow<Resource<String>>

    // Products
    fun getAllProducts(): Flow<Resource<List<CustomProduct>>>

    // Become Seller
    fun becomeSeller(accountNo: String, type: String): Flow<Resource<Unit>>
}