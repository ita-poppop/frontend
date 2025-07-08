package com.ita.poppop

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.e("checkStartFlow", "GlobalApplication")

        // Kakao SDK 초기화
        KakaoSdk.init(this, "dba46fac2339b736d14fbb068a3831cd")

    }
}