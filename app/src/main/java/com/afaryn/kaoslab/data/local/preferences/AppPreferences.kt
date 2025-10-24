package com.afaryn.kaoslab.data.local.preferences

import android.content.Context
import androidx.core.content.edit

object AppPreferences {
    private const val PREF_NAME = "app_prefs"
    private const val KEY_NOTIF_ENABLED = "notification_enabled"

    fun setNotificationEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_NOTIF_ENABLED, enabled) }
    }

    fun isNotificationEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_NOTIF_ENABLED, true)
    }
}