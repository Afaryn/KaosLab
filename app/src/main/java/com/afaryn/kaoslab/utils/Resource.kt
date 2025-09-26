package com.afaryn.kaoslab.utils

sealed class Resource<out T>(
    val data: T? = null,
    val error: String? = null
){
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(error: String): Resource<T>(error = error)
    object Loading : Resource<Nothing>()
}