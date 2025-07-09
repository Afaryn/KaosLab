package com.afaryn.kaoslab.utils

sealed class UiState<T>(
    val data: T? = null,
    val error: String? = null,
    val isLoading: Boolean? = false
){
    class Success<T>(data: T): UiState<T>(data)
    class Error<T>(error: String): UiState<T>(error = error)
    class Loading<T>(isLoading: Boolean): UiState<T>(isLoading = isLoading)

}