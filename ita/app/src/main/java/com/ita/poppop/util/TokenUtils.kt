package com.ita.poppop.util

import android.content.Context

object TokenUtils {
    // 토큰 저장
    fun saveToken(context: Context, tokenName: String, tokenKey: String, token: String) {
        val prefs = context.getSharedPreferences(tokenName, Context.MODE_PRIVATE)
        prefs.edit().putString(tokenKey, token).apply()
    }

    // 토큰 가져오기
    fun getToken(context: Context, tokenName: String, tokenKey: String): String? {
        val prefs = context.getSharedPreferences(tokenName, Context.MODE_PRIVATE)
        return prefs.getString(tokenKey, null)
    }

    // 토큰 삭제
    fun clearToken(context: Context, tokenName: String, tokenKey: String) {
        val prefs = context.getSharedPreferences(tokenName, Context.MODE_PRIVATE)
        prefs.edit().remove(tokenKey).apply()
    }
}