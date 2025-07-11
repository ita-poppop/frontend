package com.ita.poppop.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.ita.poppop.data.remote.api.SignupRequest
import com.ita.poppop.data.remote.repository.Member.MemberRepository
import com.ita.poppop.data.remote.repository.Member.MemberRepositoryImpl
import com.ita.poppop.util.LoginManager
import com.ita.poppop.util.LoginManager.User
import com.ita.poppop.util.TokenManager
import com.ita.poppop.util.TokenUtils
import com.ita.poppop.util.remote.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MainAViewModel: ViewModel() {
    private val repository: MemberRepository = MemberRepositoryImpl(RetrofitClient.memberApi)
    // 서버 토큰
    private val _tokenPair = MutableLiveData<Pair<String?, String?>>() // access, refresh
    val tokenPair: LiveData<Pair<String?, String?>> = _tokenPair

    fun setTokenPair(access: String?,refresh: String?) {
        _tokenPair.value = Pair(access, refresh)
    }


    // 로그인 상태를 나타내는 LiveData
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    // 로딩 상태
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // 에러 메시지
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    sealed class LoginState {
        object Loading : LoginState()
        object LoggedIn : LoginState()
        object LoggedOut : LoginState()
        data class Error(val message: String) : LoginState()
    }

    init {
        // 초기 로그인 상태 확인
        checkLoginStatus()
    }

    /**
     * 로그인 상태 확인
     */
    fun checkLoginStatus() {
        _loginState.value = LoginState.Loading
        _isLoading.value = true

        LoginManager.getInstance().checkLoginStatus(object : LoginManager.LoginStatusCallback {

            override fun onResult(isLoggedIn: User?) {
                _isLoading.value = false

                _loginState.value = if (isLoggedIn != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            // SignupRequest 객체 생성
                            val signupRequest = SignupRequest(
                                providerId = isLoggedIn.userId,
                                registerId = isLoggedIn.registerId,
                                nickName = isLoggedIn.userName,
                                email = isLoggedIn.userEmail,
                                profileImage = isLoggedIn.userProfile
                            )

                            val result = withContext(Dispatchers.IO) {
                                repository.postSignup(signupRequest)
                            }

                            if (result.isSuccessful) {
                                setTokenPair(result.body()!!.data.accessTocken,result.body()!!.data.refreshTocken)
                                Log.e("checkLogin", "Token: ${tokenPair.value}")
                                _loginState.value = LoginState.LoggedIn
                            } else {
                                _loginState.value = LoginState.LoggedOut
                            }
                        } catch (e: HttpException) {
                            // HTTP 에러 상세 정보
                            _loginState.value = LoginState.LoggedOut
                            Log.e("checkLogin", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
                        } catch (e: Exception) {
                            Log.e("checkLogin", "Exception: ${e.message}", e)
                            _loginState.value = LoginState.LoggedOut
                        }
                    }
                    LoginState.Loading // 비동기 작업 중이므로 Loading 상태 유지
                } else {
                    LoginState.LoggedOut
                }
            }
        })
    }

    /**
     * 로그아웃 처리
     */
    fun logout() {
        _isLoading.value = true
        LoginManager.getInstance().logout {
            _isLoading.value = false
            _loginState.value = LoginState.LoggedOut
        }
    }

    fun unlink() {
        _isLoading.value = true
        LoginManager.getInstance().unlink {
            _isLoading.value = false
            _loginState.value = LoginState.LoggedOut
        }
    }


    /**
     * 로그인 성공 시 호출
     */
    fun onLoginSuccess() {
        _loginState.value = LoginState.LoggedIn
    }

    /**
     * 에러 메시지 클리어
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
