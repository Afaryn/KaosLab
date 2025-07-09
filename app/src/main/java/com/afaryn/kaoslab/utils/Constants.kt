package com.afaryn.kaoslab.utils

import android.Manifest

object Constants {

    const val PRODUCT_COLLECTION = "product"


    val PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}