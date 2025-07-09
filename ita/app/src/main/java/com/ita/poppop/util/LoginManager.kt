package com.ita.poppop.util


import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ita.poppop.R
import com.ita.poppop.data.remote.repository.Member.MemberRepository
import com.ita.poppop.data.remote.repository.Member.MemberRepositoryImpl
import com.ita.poppop.util.remote.RetrofitClient
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 카카오 로그인과 구글 로그인 상태를 통합적으로 관리하는 클래스
 */
class LoginManager(
) {
    private lateinit var credentialManager: CredentialManager
    private lateinit var auth: FirebaseAuth
    init {
        auth = FirebaseAuth.getInstance()
    }
    /**
     * 로그인 상태 확인 결과를 받는 콜백 인터페이스
     */

    data class User(
        val userId : String,
        val registerId : String,
        val userName : String,
        val userEmail : String,
        val userProfile : String,
    )

    interface SignInStatusCallback {
        fun onResult(isSignIn: User?)
    }

    interface LoginStatusCallback {
        fun onResult(isLoggedIn: User?)
    }
    fun signInWithKakao(callback: SignInStatusCallback,context: Context){

        val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.d("checkLogin","11111111")
                callback.onResult(null)
            } else if (token != null) {
                getKakaoUserInfo { user ->
                    if (user != null) {
                        // 사용자 정보 사용
                        Log.d("checkLogin","2222222")
                        callback.onResult(user)
                    } else {
                        // 에러 처리
                        Log.d("checkLogin","3333333")
                        callback.onResult(null)
                    }
                }
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
                } else if (token != null) {
                    getKakaoUserInfo { user ->
                        if (user != null) {
                            // 사용자 정보 사용
                            Log.d("checkLogin","2222222")
                            callback.onResult(user)
                        } else {
                            // 에러 처리
                            Log.d("checkLogin","3333333")
                            callback.onResult(null)
                        }
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
        }
    }

    private fun getKakaoUserInfo(callback: (User?) -> Unit) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("LoginLOG", "사용자 정보 요청 실패", error)
                callback(null)  // 콜백으로 null 전달
                return@me  // 람다에서 조기 종료
            }

            // 성공 시 사용자 정보 처리
            val userData = user?.let {
                User(
                    userId = user.id.toString(),
                    registerId = "KAKAO",
                    userName = user.kakaoAccount?.profile?.nickname.toString(),
                    userEmail = user.kakaoAccount?.email.toString(),
                    userProfile = user.kakaoAccount?.profile?.profileImageUrl.toString()
                )
            }
            callback(userData)  // 콜백으로 결과 전달
        }
    }


    fun signInWithGoogle(callback: SignInStatusCallback,context: Context){
        credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id)) // 또는 R.string.default_web_client_id
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                val credential = result.credential

                when (credential) {
                    is GoogleIdTokenCredential -> {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val credential =
                            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
//                                    Log.d(TAG, "signInWithCredential:success")
                                    getGoogleUserInfo { user ->
                                        if (user != null) {
                                            // 사용자 정보 사용
                                            callback.onResult(user)
                                        } else {
                                            // 에러 처리
                                            callback.onResult(null)
                                        }
                                    }
                                } else {
                                    // If sign in fails, display a message to the user
//                                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    callback.onResult(null)
                                }
                            }
                    }

                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            try {
                                val googleIdTokenCredential =
                                    GoogleIdTokenCredential.createFrom(credential.data)
                                val credential = GoogleAuthProvider.getCredential(
                                    googleIdTokenCredential.idToken,
                                    null
                                )
                                auth.signInWithCredential(credential)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Sign in success, update UI with the signed-in user's information
                                            getGoogleUserInfo { user ->
                                                if (user != null) {
                                                    // 사용자 정보 사용
                                                    callback.onResult(user)
                                                } else {
                                                    // 에러 처리
                                                    callback.onResult(null)
                                                }
                                            }
                                        } else {
                                            // If sign in fails, display a message to the user
//                                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                                            callback.onResult(null)
                                        }
                                    }
                            } catch (e: Exception) {
//                                showSignInError("인증 정보 처리 중 오류가 발생했습니다")
                                callback.onResult(null)
                            }
                        } else {
//                            showSignInError("지원하지 않는 인증 정보 유형입니다")
                            callback.onResult(null)
                        }
                    }

                    else -> {
//                        showSignInError("예상치 못한 인증 정보 유형: ${credential::class.java.simpleName}")
                        callback.onResult(null)
                    }
                }

            } catch (e: GetCredentialException) {
                callback.onResult(null)
            }
        }
    }

    private fun getGoogleUserInfo(callback: (User?) -> Unit) {
        val user = auth.currentUser
        val userData = user?.let {
            User(
                userId = user!!.uid,
                registerId = "GOOGLE",
                userName = user.displayName.toString(),
                userEmail = user.email.toString(),
                userProfile = user.photoUrl.toString()
            )
        }
        callback(userData)
    }


    /**
     * 카카오 로그인과 구글 로그인 상태를 확인하여 OR 연산으로 처리
     * 둘 중 하나라도 로그인되어 있으면 true를 반환
     */
    fun checkLoginStatus(callback: LoginStatusCallback) {
        checkGoogleLoginStatus{ isGoogleLoggedIn ->
            checkKakaoLoginStatus { isKakaoLoggedIn ->
                // OR 연산: 둘 중 하나라도 로그인되어 있으면 true
                val finalResult = isGoogleLoggedIn || isKakaoLoggedIn
                if(finalResult){
                    getGoogleUserInfo { user ->
                        if (user != null) {
                            // 사용자 정보 사용
                            callback.onResult(user)
                        } else {
                            getKakaoUserInfo { user ->
                                if (user != null) {
                                    // 사용자 정보 사용
                                    callback.onResult(user)
                                } else {
                                    // 에러 처리
                                    callback.onResult(null)
                                }
                            }
                        }
                    }
                }else{
                    callback.onResult(null)
                }

            }
        }
    }


    private fun checkGoogleLoginStatus(callback: (Boolean) -> Unit) {
        if(FirebaseAuth.getInstance().currentUser != null){
            callback(true)
        }else{
            callback(false)
        }
    }

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
                    callback(true)
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