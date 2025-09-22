package com.afaryn.kaoslab.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Kurir(
    val id:String = UUID.randomUUID().toString().replace("-", "").substring(0, 20),
    val name:String?=null,
    val logo : String?=null,
) :Parcelable
