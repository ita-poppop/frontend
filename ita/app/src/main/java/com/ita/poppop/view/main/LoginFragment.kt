package com.ita.poppop.view.main

import android.util.Log
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.data.remote.api.SignupRequest
import com.ita.poppop.data.remote.repository.Member.MemberRepository
import com.ita.poppop.data.remote.repository.Member.MemberRepositoryImpl
import com.ita.poppop.databinding.FragmentLoginBinding
import com.ita.poppop.util.LoginManager
import com.ita.poppop.util.remote.RetrofitClient
import com.ita.poppop.viewmodel.MainAViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class LoginFragment: BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {
    private lateinit var mainAViewModel: MainAViewModel
    private val repository: MemberRepository = MemberRepositoryImpl(RetrofitClient.memberApi)
    private lateinit var credentialManager: CredentialManager
    private lateinit var auth: FirebaseAuth

    override fun initView() {
        setupViewModel()
        setupGoogle()
        setupWindowInsets()
        setupClick()


    }

    private fun setupViewModel(){
        mainAViewModel = ViewModelProvider(requireActivity())[MainAViewModel::class.java]
    }

    private fun setupGoogle() {
        credentialManager = CredentialManager.create(requireContext())
        auth = FirebaseAuth.getInstance()
    }

    private fun setupClick(){
        binding.apply {
            btLoginKakao.setOnClickListener {
                LoginManager.getInstance().signInWithKakao(object : LoginManager.SignInStatusCallback {
                    override fun onResult(isSignIn: LoginManager.User?) {
                        if(isSignIn != null){
                            lifecycleScope.launch {
                                try {
                                    // SignupRequest 객체 생성
                                    val signupRequest = SignupRequest(
                                        providerId = isSignIn.userId,
                                        registerId = isSignIn.registerId,
                                        nickName = isSignIn.userName,
                                        email = isSignIn.userEmail,
                                        profileImage = isSignIn.userProfile
                                    )

                                    val result = withContext(Dispatchers.IO) {
                                        repository.postSignup(signupRequest)
                                    }

                                    if (result.isSuccessful) {
                                        mainAViewModel.setTokenPair(result.body()!!.data.accessTocken,result.body()!!.data.refreshTocken)
                                        Log.d("checkLogin", "Token: ${mainAViewModel.tokenPair.value}")
                                        mainAViewModel.onLoginSuccess()
                                    }
                                } catch (e: HttpException) {
                                    // HTTP 에러 상세 정보
                                    Log.e("checkLogin", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
                                } catch (e: Exception) {
                                    Log.e("checkLogin", "Exception: ${e.message}", e)
                                }
                            }

                        }else{
                            Log.d("checkLogin"," Kakao isSignIn : 카카오 로그인 실패")

                        }
                    }
                },requireContext())
            }
            btLoginGoogle.setOnClickListener {
                LoginManager.getInstance().signInWithGoogle(object : LoginManager.SignInStatusCallback {
                    override fun onResult(isSignIn: LoginManager.User?) {
                        if(isSignIn != null){
                            Log.d("checkLogin","Google isSignIn : ${isSignIn}")
                            lifecycleScope.launch {
                                try {
                                    // SignupRequest 객체 생성
                                    val signupRequest = SignupRequest(
                                        providerId = isSignIn.userId,
                                        registerId = isSignIn.registerId,
                                        nickName = isSignIn.userName,
                                        email = isSignIn.userEmail,
                                        profileImage = isSignIn.userProfile
                                    )

                                    val result = withContext(Dispatchers.IO) {
                                        repository.postSignup(signupRequest)
                                    }

                                    if (result.isSuccessful) {
                                        mainAViewModel.setTokenPair(result.body()!!.data.accessTocken,result.body()!!.data.refreshTocken)
                                        Log.d("checkLogin", "Token: ${mainAViewModel.tokenPair.value}")
                                        mainAViewModel.onLoginSuccess()
                                    }
                                } catch (e: HttpException) {
                                    // HTTP 에러 상세 정보
                                    Log.e("checkLogin", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
                                } catch (e: Exception) {
                                    Log.e("checkLogin", "Exception: ${e.message}", e)
                                }
                            }
                        }else{
                            Log.d("checkLogin"," Google isSignIn : false")
                        }
                    }
                },requireContext())
            }
        }
    }

    //    private fun signInWithKakao(){
//        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//            processKakaoIdToken(token, error)
//        }
//        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
//        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
//            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
//                if (error != null) {
//                    Log.d("checkLogin", "카카오톡으로 로그인 실패", error)
//
//                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
//                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리
//                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
//                        return@loginWithKakaoTalk
//                    }
//
//                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
//                    UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
//                    processKakaoIdToken(token,error)
//                } else if (token != null) {
//                    Log.d("checkLogin", "카카오톡으로 로그인 성공 ${token.accessToken}")
//
//                }
//            }
//        } else {
//            Log.d("checkLogin", "카카오톡 미설치 - 카카오계정으로 로그인")
//            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
//        }
//    }
//
//    private fun signInWithGoogle() {
//        val googleIdOption = GetGoogleIdOption.Builder()
//            .setFilterByAuthorizedAccounts(false)
//            .setServerClientId(getString(R.string.web_client_id)) // 또는 R.string.default_web_client_id
//            .build()
//
//        val request = GetCredentialRequest.Builder()
//            .addCredentialOption(googleIdOption)
//            .build()
//
//        lifecycleScope.launch {
//            try {
//                val result = credentialManager.getCredential(
//                    request = request,
//                    context = requireContext(),
//                )
//                handleSignIn(result)
//            } catch (e: GetCredentialException) {
//                handleGoogleFailure(e)
//            }
//        }
//    }
//
//    private fun handleSignIn(result: GetCredentialResponse) {
//        val credential = result.credential
//
//        when (credential) {
//            is GoogleIdTokenCredential -> {
//                processGoogleIdToken(credential.idToken)
//            }
//            is CustomCredential -> {
//                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
//                    try {
//                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
//                        processGoogleIdToken(googleIdTokenCredential.idToken)
//                    } catch (e: Exception) {
//                        showSignInError("인증 정보 처리 중 오류가 발생했습니다")
//                    }
//                } else {
//                    showSignInError("지원하지 않는 인증 정보 유형입니다")
//                }
//            }
//            else -> {
//                showSignInError("예상치 못한 인증 정보 유형: ${credential::class.java.simpleName}")
//            }
//        }
//    }
//
//    private fun processGoogleIdToken(idToken: String) {
//        Log.d("checkLogin", "Processing Google ID Token")
//        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(firebaseCredential)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//                    Log.d("checkLogin", "Firebase signInWithCredential: success")
//                    showGoogleSignInSuccess(auth.currentUser)
//                } else {
//                    Log.d("checkLogin", "Firebase signInWithCredential: failure", task.exception)
//                    showSignInError("Firebase 인증에 실패했습니다: ${task.exception?.message}")
//                }
//            }
//    }
//
//
//    private fun handleGoogleFailure(e: GetCredentialException) {
//        Log.d("checkLogin", "GetCredential failed", e)
//        when (e) {
//            is GetCredentialCancellationException -> {
//                Log.d("checkLogin", "User cancelled the sign-in flow")
//                showSignInError("로그인이 취소되었습니다")
//            }
//            is NoCredentialException -> {
//                Log.d("checkLogin", "No Google accounts available")
//                showSignInError("사용 가능한 Google 계정이 없습니다")
//            }
//            else -> {
//                Log.d("checkLogin", "Unexpected credential manager exception: ${e::class.java.simpleName}", e)
//                showSignInError("로그인 중 오류가 발생했습니다: ${e.message}")
//            }
//        }
//    }
//
//    private fun processKakaoIdToken(token: OAuthToken?, error: Throwable?) {
//        if (error != null) {
//            handleKakoaFailure(error)
//        } else if (token != null) {
//            Log.d("checkLogin", "카카오계정으로 로그인 성공 ${token.accessToken}")
//            showKakoaSignInSuccess()
//        }
//    }
//
//    private fun handleKakoaFailure(e: Throwable?) {
//        Log.d("checkLogin", "카카오계정으로 로그인 실패")
//        showSignInError("카카오계정으로 로그인 실패: ${e?.message}")
//    }
//
//    private fun showGoogleSignInSuccess(user: FirebaseUser?) {
//        lifecycleScope.launch {
//            try {
//                // SignupRequest 객체 생성
//                val signupRequest = SignupRequest(
//                    providerId = user!!.uid,
//                    registerId = "GOOGLE",
//                    nickName = user.displayName.toString(),
//                    email = user.email.toString(),
//                    profileImage = user.photoUrl.toString()
//                )
//
//                val result = withContext(Dispatchers.IO) {
//                    repository.postSignup(signupRequest)
//                }
//
//                if (result.isSuccessful) {
//                    Log.d("checkLoginEx","서버 전송 성공 ${result.body()}")
//                    mainAViewModel.onLoginSuccess()
//                }
//            } catch (e: HttpException) {
//                // HTTP 에러 상세 정보
//                Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
//            } catch (e: Exception) {
//                Log.e("API_ERROR", "Exception: ${e.message}", e)
//            }
//        }
//
//    }
//    private fun showKakoaSignInSuccess() {
//        // 사용자 정보 요청 (기본)
//        UserApiClient.instance.me { user, error ->
//            if (error != null) {
//                Log.e("LoginLOG", "사용자 정보 요청 실패", error)
//            } else if (user != null) {
//                Log.i("LoginLOG", "사용자 정보 요청 성공")
//
//                lifecycleScope.launch {
//                    try {
//                        // SignupRequest 객체 생성
//                        val signupRequest = SignupRequest(
//                            providerId =user.id.toString(),
//                            registerId = "KAKAO",
//                            nickName = user.kakaoAccount?.profile?.nickname.toString(),
//                            email = user.kakaoAccount?.email.toString(),
//                            profileImage = user.kakaoAccount?.profile?.profileImageUrl.toString()
//                        )
//
//                        val result = withContext(Dispatchers.IO) {
//                            repository.postSignup(signupRequest)
//                        }
//
//                        if (result.isSuccessful) {
//                            Log.d("checkLoginEx","서버 전송 성공 ${result.body()}")
//                            mainAViewModel.onLoginSuccess()
//                        }
//                    } catch (e: HttpException) {
//                        // HTTP 에러 상세 정보
//                        Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
//                    } catch (e: Exception) {
//                        Log.e("API_ERROR", "Exception: ${e.message}", e)
//                    }
//                }
//
//
//            }
//        }
//
//    }
//
//    private fun showSignInError(message: String) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
//        Log.e(TAG, "Sign-in error: $message")
//    }
    companion object {
        private const val TAG = "LoginFragment"
    }
}