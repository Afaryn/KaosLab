package com.afaryn.kaoslab.utils

sealed class Validation() {
    object Success: Validation()
    data class Failed(val message: String): Validation()
}

data class FieldsState(
    val email: Validation,
    val password: Validation
)