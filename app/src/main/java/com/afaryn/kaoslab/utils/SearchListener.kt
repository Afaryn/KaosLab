package com.afaryn.kaoslab.utils

import android.content.Context

interface SearchListener {
    fun onSearch(query: String)
    fun triggerSearchView(isOpen: Boolean)

    companion object {
        fun runtimeException(context: Context): String {
            return "$context must implement SearchListener"
        }
    }
}