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
import androidx.lifecycle.ViewModelProvider
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
import com.ita.poppop.viewmodel.MainAViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class LoginFragment: BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {
    private lateinit var mainAViewModel: MainAViewModel

    private lateinit var credentialManager: CredentialManager
    private lateinit var auth: FirebaseAuth

    override fun initView() {
        Log.e("checkStartFlow", "LoginFragment")
        credentialManager = CredentialManager.create(requireContext())
        auth = FirebaseAuth.getInstance()
        mainAViewModel = ViewModelProvider(requireActivity())[MainAViewModel::class.java]
        setupWindowInsets()
        binding.apply {
            // 카카오계정으로 로그인 공통 callback 구성
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.d("checkLogin", "카카오계정으로 로그인 실패")
                } else if (token != null) {
                    Log.d("checkLogin", "카카오계정으로 로그인 성공 ${token.accessToken}")
                    mainAViewModel.onLoginSuccess()
                    getGoogleUserInfo() 
//                    saveSessionIdToPrefs(token.accessToken)
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
                            mainAViewModel.onLoginSuccess()
//                            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
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
            mainAViewModel.onLoginSuccess()
            Log.d("LoginLOG", "googleLogin ID: ${user.uid}")
            Log.d("LoginLOG", "googleLogin 닉네임: ${user.displayName}")
            Log.d("LoginLOG", "googleLogin 이메일: ${user.email}")
            Log.d("LoginLOG", "googleLogin 프로필 이미지: ${user.photoUrl}")
            // 메인 화면으로 이동
//            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
        }
    }
    private fun getKakaoUserInfo(user: FirebaseUser?) {
        // 사용자 정보 요청 (기본)
        user?.let {
            mainAViewModel.onLoginSuccess()
            Log.d("LoginLOG", "googleLogin ID: ${user.uid}")
            Log.d("LoginLOG", "googleLogin 닉네임: ${user.displayName}")
            Log.d("LoginLOG", "googleLogin 이메일: ${user.email}")
            Log.d("LoginLOG", "googleLogin 프로필 이미지: ${user.photoUrl}")
            // 메인 화면으로 이동
//            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
        }
    }
    private fun getGoogleUserInfo() {
        // 사용자 정보 요청 (기본)
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("LoginLOG", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i("LoginLOG", "사용자 정보 요청 성공")

                // 사용자 정보 활용

                Log.d("LoginLOG", "KakaoLogin ID: ${user.id}")
                Log.d("LoginLOG", "KakaoLogin 닉네임: ${user.kakaoAccount?.profile?.nickname}")
                Log.d("LoginLOG", "KakaoLogin 프로필 이미지: ${user.kakaoAccount?.profile?.profileImageUrl}")

            }
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