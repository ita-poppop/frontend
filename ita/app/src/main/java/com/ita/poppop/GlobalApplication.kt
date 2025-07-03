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

        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError() == true) {
                        //로그인 필요
                        Log.e(TAG, "로그인 필요")
                    }
                    else {
                        //기타 에러
                        Log.e(TAG, "기타 에러")
                    }
                }
                else {
                    //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                    Log.e(TAG, "토큰 유효성 체크 성공")
                }
            }
        }
        else {
            //로그인 필요
            Log.e(TAG, "로그인 필요")
        }

    }
}