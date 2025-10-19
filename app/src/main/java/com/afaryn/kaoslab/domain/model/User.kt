package com.afaryn.kaoslab.domain.model

import android.os.Parcelable
import com.afaryn.kaoslab.utils.Constants.CUSTOMER
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class User(
    var id: String = UUID.randomUUID().toString().replace("-", "").substring(0, 10),
    val name: String? = null,
    val email: String? = null,
    val profilePicture: String = "",
    val role: String = CUSTOMER,
    val phone: String = "",
    val accountNo: String = "",
    val createdAt: Timestamp = Timestamp.now()
) : Parcelable
