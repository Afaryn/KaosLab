package com.afaryn.kaoslab.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Review(
    val id:String = UUID.randomUUID().toString().replace("-", "").substring(0, 20),
    val user:String?=null,
    val review : String?=null,
    val createdAt: Date?=null,
    val imageUrl : String?=null,
    val rate:Int?=0
) :Parcelable
