package com.ita.poppop.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ita.poppop.util.LoginManager

class MainAViewModel: ViewModel() {
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
            override fun onResult(isLoggedIn: Boolean) {
                _isLoading.value = false
                _loginState.value = if (isLoggedIn) {
                    LoginState.LoggedIn
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
