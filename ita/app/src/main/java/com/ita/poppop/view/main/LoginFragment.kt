package com.ita.poppop.view.main

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.databinding.FragmentLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class LoginFragment: BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {
    private lateinit var credentialManager: CredentialManager
    private lateinit var auth: FirebaseAuth

    // 앱 -> 서버 : 프리퍼런스에 저장된 세션ID를 통해 서버측에 전달
    // 서버 -> 앱 : 서버측에서 해당 세션ID의 유효성 전달
        // 앱 : 유효 : MainFragment로 이동
        // 앱 : 무효 : LoginFragment로 이동
            // 앱 -> 카카오(구글) :  로그인 요청
            // 카카오(구글) -> 앱 : 로그인 화면 요청
            // 앱 -> 카카오(구글) : 로그인 확인
            // 카카오(구글) -> 앱 : 카카오(구글) 토큰 발급
            // 앱  -> 서버 : 카카오(구글) 토큰 전달
            // 서버 -> 카카오(구글) : 카카오(구글) 토큰으로 카카오(구글)에 사용자 정보 조회
            // 카카오(구글) -> 서버 : 사용자 정보 제공
            // 서버 : 사용자DB에 사용자 생성 및 갱신(세션ID,카카오(구글)토큰{유효성})
            // 서버 -> 앱 : 세션 ID 제공
            // 앱 : 세션ID를 프리퍼런스에 저장
            // 앱 : MainFragment로 이동



//    [1] (android)앱 : 시작 시 SharedPreferences에 저장된 토큰 조회
//
//    [2] (android)앱 : 해당 토큰 유효성 확인
//
//      ✅ 유효: 바로 MainFragment로 이동
//
//      ❌ 무효: LoginFragment로 이동
//
//      [3] 앱 → 카카오 SDK로 로그인 요청
//
//      [4] 카카오 SDK가 앱 내에서 로그인 UI를 띄움
//
//      [5] 토큰(access token) 발급받음 → 서버에 전달
//
//      [6] 서버가 access token을 바탕으로 사용자 정보 조회
//
//      [7] 서버에서 access token으로 카카오 서버에서 사용자 정보 요청후 사용자 DB 생성
//
//      [8] 사용자 DB 생성 완료 생성한 사용자 정보 앱에 전달
//
//      [9] 앱에서 완료 메시지 받은 후 MainFragment로 이동 맟 액세스 토큰 및 리프레스 토큰 프리퍼런스에 저장

    override fun initView() {
        Log.e("checkStartFlow", "LoginFragment")
        credentialManager = CredentialManager.create(requireContext())
        auth = FirebaseAuth.getInstance()

        setupWindowInsets()
        binding.apply {
            // 카카오계정으로 로그인 공통 callback 구성
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.d("checkLogin", "카카오계정으로 로그인 실패")
                } else if (token != null) {
                    Log.d("checkLogin", "카카오계정으로 로그인 성공 ${token.accessToken}")

//                    saveSessionIdToPrefs(token.accessToken)
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                }
            }

            btLoginKakao.setOnClickListener {
                // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                    UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                        if (error != null) {
                            Log.d("checkLogin", "카카오톡으로 로그인 실패", error)

                            // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                            // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                return@loginWithKakaoTalk
                            }

                            // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
                        } else if (token != null) {
                            Log.d("checkLogin", "카카오톡으로 로그인 성공 ${token.accessToken}")
                            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                        }
                    }
                } else {
                    Log.d("checkLogin", "카카오톡 미설치 - 카카오계정으로 로그인")
                    UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
                }
            }

            btLoginGoogle.setOnClickListener {
                signInWithGoogle()
            }
        }
    }

    private fun signInWithGoogle() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.web_client_id)) // 또는 R.string.default_web_client_id
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireContext(),
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                handleFailure(e)
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential

        // 디버깅을 위한 로그 추가
        Log.d("checkLogin", "구글계정 : Received credential type: ${credential::class.java.simpleName}")

        when (credential) {
            is GoogleIdTokenCredential -> {
                Log.d("checkLogin", "구글계정 : Direct GoogleIdTokenCredential received")
                processGoogleIdToken(credential.idToken)
            }
            is CustomCredential -> {
                Log.d("checkLogin", "구글계정 : CustomCredential received with type: ${credential.type}")
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        Log.d("checkLogin", "Successfully extracted GoogleIdTokenCredential from CustomCredential")
                        processGoogleIdToken(googleIdTokenCredential.idToken)
                    } catch (e: Exception) {
                        Log.d("checkLogin", "Failed to extract GoogleIdTokenCredential from CustomCredential")
                        showSignInError("인증 정보 처리 중 오류가 발생했습니다")
                    }
                } else {
                    Log.d("checkLogin", "Unexpected CustomCredential type: ${credential.type}")
                    showSignInError("지원하지 않는 인증 정보 유형입니다")
                }
            }
            else -> {
                Log.d("checkLogin", "Unexpected credential type: ${credential::class.java.name}")
                showSignInError("예상치 못한 인증 정보 유형: ${credential::class.java.simpleName}")
            }
        }
    }

    private fun processGoogleIdToken(idToken: String) {
        Log.d("checkLogin", "Processing Google ID Token")
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("checkLogin", "Firebase signInWithCredential: success")
                    val user = auth.currentUser
                    showSignInSuccess(user)
                } else {
                    Log.d("checkLogin", "Firebase signInWithCredential: failure", task.exception)
                    showSignInError("Firebase 인증에 실패했습니다: ${task.exception?.message}")
                }
            }
    }

    private fun handleFailure(e: GetCredentialException) {
        Log.d("checkLogin", "GetCredential failed", e)
        when (e) {
            is GetCredentialCancellationException -> {
                Log.d("checkLogin", "User cancelled the sign-in flow")
                showSignInError("로그인이 취소되었습니다")
            }
            is NoCredentialException -> {
                Log.d("checkLogin", "No Google accounts available")
                showSignInError("사용 가능한 Google 계정이 없습니다")
            }
            else -> {
                Log.d("checkLogin", "Unexpected credential manager exception: ${e::class.java.simpleName}", e)
                showSignInError("로그인 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    private fun showSignInSuccess(user: FirebaseUser?) {
        user?.let {
            val message = "환영합니다, ${it.displayName ?: "사용자"}님!"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            Log.d("checkLogin", "Login successful for user: ${it.uid}")

            // 메인 화면으로 이동
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
        }
    }

    private fun showSignInError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        Log.e(TAG, "Sign-in error: $message")
    }
    private fun saveSessionIdToPrefs(sessionId: String) {
        val prefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit().putString("session_id", sessionId).apply()
    }
    companion object {
        private const val TAG = "LoginFragment"
    }
}