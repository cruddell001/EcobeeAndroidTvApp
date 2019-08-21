package com.bigcommerce.mytvapp.persistence

import android.content.Context

object SharedPrefsHelper {
    const val FILENAME = "ecobee_prefs"
    const val PREF_TOKEN = "token"

    private fun getSharedPrefs(context: Context) = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)

    fun getToken(context: Context): String? {
        return getSharedPrefs(context).getString(PREF_TOKEN, null)
    }

    fun setToken(context: Context, token: String) = getSharedPrefs(context).edit().putString(PREF_TOKEN, token).commit()
}