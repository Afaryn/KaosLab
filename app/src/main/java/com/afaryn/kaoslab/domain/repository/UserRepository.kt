package com.afaryn.kaoslab.domain.repository

import com.afaryn.kaoslab.domain.model.CartProduct
import com.afaryn.kaoslab.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun addToCart(cartProduct: CartProduct): Flow<Resource<Unit>>
}