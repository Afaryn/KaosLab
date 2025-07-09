package com.afaryn.kaoslab.utils

import android.Manifest

object Constants {

    const val PRODUCT_COLLECTION = "product"
    const val CUSTOM_PRODUCT_COLLECTION = "customproduct"


    val PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}