package com.example.reacht_android.network

import android.content.Context

object ServerPreferences {
    private const val PREFS_NAME = "server_prefs"
    private const val KEY_IP = "server_ip"

    fun getIp(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_IP, null)
    }

    fun saveIp(context: Context, ip: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_IP, ip)
            .apply()
    }
}
