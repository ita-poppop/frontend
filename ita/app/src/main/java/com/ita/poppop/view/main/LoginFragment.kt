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

    companion object {
        private const val TAG = "LoginFragment"
    }
}