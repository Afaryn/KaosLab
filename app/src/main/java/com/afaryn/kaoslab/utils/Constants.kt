package com.afaryn.kaoslab.utils

import android.Manifest

object Constants {

    const val PRODUCT_COLLECTION = "product"
    const val CUSTOM_PRODUCT_COLLECTION = "customproduct"
    const val COLL_CART = "cart"
    const val COLL_USER = "users"
    const val COLL_ADDRESS = "address"


    val PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    const val CUSTOMER = "customer"
    const val OWNER = "owner"
    const val DESIGNER = "designer"

    const val PENDING_STATUS = "pending"
    const val PROCESSING_STATUS = "processing"
    const val SHIPPED_STATUS = "shipped"
    const val DELIVERED_STATUS = "delivered"
}