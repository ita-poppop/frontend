package com.ita.poppop.util

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.KakaoSdkError

/**
 * 카카오 로그인과 구글 로그인 상태를 통합적으로 관리하는 클래스
 */
class LoginManager {

    /**
     * 로그인 상태 확인 결과를 받는 콜백 인터페이스
     */
    interface LoginStatusCallback {
        fun onResult(isLoggedIn: Boolean)
    }

    /**
     * 카카오 로그인과 구글 로그인 상태를 확인하여 OR 연산으로 처리
     * 둘 중 하나라도 로그인되어 있으면 true를 반환
     */
    fun checkLoginStatus(callback: LoginStatusCallback) {
        // 구글 로그인 상태 확인
        val googleUser = FirebaseAuth.getInstance().currentUser
        if(googleUser!=null){
            Log.d("checkLoginState","자동 login for 구글")
            // 서버 사용자 계정 갱신
        }
        val isGoogleLoggedIn = googleUser != null

        // 카카오 로그인 상태 확인
        checkKakaoLoginStatus { isKakaoLoggedIn ->
            // OR 연산: 둘 중 하나라도 로그인되어 있으면 true
            val finalResult = isGoogleLoggedIn || isKakaoLoggedIn
            callback.onResult(finalResult)
        }
    }

    /**
     * 카카오 로그인 상태만 확인하는 메서드
     */
    private fun checkKakaoLoginStatus(callback: (Boolean) -> Unit) {
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error != null) {
                    if (error is KakaoSdkError && error.isInvalidTokenError() == true) {
                        // 로그인 필요
                        callback(false)
                    } else {
                        // 기타 에러
                        callback(false)
                    }
                } else {
                    // 토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                    callback(true)
                    Log.d("checkLoginState","자동 login for 카카오")
                    // 서버 사용자 계정 갱신
                }
            }
        } else {
            // 로그인 필요
            callback(false)
        }
    }

    /**
     * 전체 로그아웃 (카카오 + 구글)
     */
    fun logout(callback: () -> Unit) {
        // 카카오 로그아웃
        UserApiClient.instance.logout { error ->
            // 구글 로그아웃
            FirebaseAuth.getInstance().signOut()
            callback()
        }
    }
    /**
     * 전체 계정 삭제 (카카오 + 구글)
     */
    fun unlink(callback: () -> Unit){
        // 카카오 계정 삭제
        UserApiClient.instance.unlink { error ->
            // 구글 계정 삭제
            FirebaseAuth.getInstance().currentUser?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseAuth", "사용자 계정 삭제 완료")
                    } else {
                        Log.e("FirebaseAuth", "사용자 계정 삭제 실패", task.exception)
                    }
                }
            callback()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginManager? = null

        fun getInstance(): LoginManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LoginManager().also { INSTANCE = it }
            }
        }
    }
}