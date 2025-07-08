package com.ita.poppop.util

import android.content.Context

class TokenManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("kakao_tokens", Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long) {
        prefs.edit().apply {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            putLong("expires_at", System.currentTimeMillis() + (expiresIn * 1000))
            apply()
        }
    }

    fun getAccessToken(): String? = prefs.getString("access_token", null)
    fun getRefreshToken(): String? = prefs.getString("refresh_token", null)

    fun isTokenExpired(): Boolean {
        val expiresAt = prefs.getLong("expires_at", 0)
        return System.currentTimeMillis() > expiresAt - 300000 // 5분 여유시간
    }
}