package com.afaryn.kaoslab.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Courier(
    val id: String = "",
    val name: String = "",
    val logo: String = ""
) : Parcelable
