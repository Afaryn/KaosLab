package com.afaryn.kaoslab.domain.repository

import com.afaryn.kaoslab.domain.model.Address
import com.afaryn.kaoslab.domain.model.CartProduct
import com.afaryn.kaoslab.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Cart
    fun addToCart(cartProduct: CartProduct): Flow<Resource<Unit>>
    fun getCartProducts(): Flow<Resource<List<CartProduct>>>
    fun deleteCartProduct(id: String): Flow<Resource<Unit>>

    // Address
    fun getLastAddress(): Flow<Resource<Address>>
    fun getAddress(): Flow<Resource<List<Address>>>
    fun createAddress(address: Address): Flow<Resource<Unit>>
    fun deleteAddress(id: String): Flow<Resource<Unit>>
}