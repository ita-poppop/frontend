package com.ita.poppop.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.ita.poppop.R
import com.ita.poppop.base.BaseActivity
import com.ita.poppop.databinding.ActivityMainBinding
import com.ita.poppop.util.TokenManager
import com.ita.poppop.viewmodel.MainAViewModel
import com.kakao.sdk.common.util.Utility
import com.ita.poppop.util.NotificationSettingsManager


class MainActivity: BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private lateinit var navGraph: NavGraph
    private lateinit var navController: NavController
    private lateinit var mainAViewModel: MainAViewModel
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("checkStartFlow", "MainActivity")
        mainAViewModel = ViewModelProvider(this)[MainAViewModel::class.java]
        tokenManager = TokenManager(this)
        Log.e("checkToken", "getAccessToken: ${tokenManager.getAccessToken()},getRefreshToken:  ${tokenManager.getRefreshToken()}")
        mainAViewModel.setTokenPair(tokenManager.getAccessToken(),tokenManager.getRefreshToken())



        binding.apply {
            // 확장된 화면 대응
            var keyHash = Utility.getKeyHash(this@MainActivity)
            Log.d("checkKey",keyHash)
            //내비게이션 바(Navigation Bar)의 명암 대비 설정을 비활성화 -> 투명화
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false

            }

            // FragmentContainerView에 동적으로 navi 연결
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.fcv_main_activity_container) as NavHostFragment

            navController = navHostFragment.navController

            val navInflater = navController.navInflater
            navGraph = navInflater.inflate(R.navigation.navi_launch)
//            checkLoginAndNavigate()
            mainAViewModel.loginState.observe(this@MainActivity, Observer { state ->
                when (state) {
                    is MainAViewModel.LoginState.Loading -> {
                        // 로딩 UI 표시
//                    showLoading()
                    }
                    is MainAViewModel.LoginState.LoggedIn -> {
                        // 메인 화면으로 이동

                        moveToMain()
                        Toast.makeText(this@MainActivity,"moveToMain",Toast.LENGTH_SHORT).show()
                    }
                    is MainAViewModel.LoginState.LoggedOut -> {
                        // 로그인 화면으로 이동
                        moveToLogin()
                        Toast.makeText(this@MainActivity,"moveToLogin",Toast.LENGTH_SHORT).show()
                    }
                    is MainAViewModel.LoginState.Error -> {
                        // 에러 처리
//                    showError(state.message)
                    }
                }
            })
        }
        initializeNotificationSettings()
    }

    private fun initializeNotificationSettings() {
        try {
            // 저장된 알림 설정을 읽어와서 적용
            NotificationSettingsManager.applyStoredSettings(this)

            android.util.Log.d("MyApplication", "알림 설정 초기화 완료")
        } catch (e: Exception) {
            android.util.Log.e("MyApplication", "알림 설정 초기화 실패: ${e.message}")
        }
    }

    private fun moveToMain() {
        navGraph.setStartDestination(R.id.mainFragment)
        navController.graph = navGraph
        navController.navigate(R.id.mainFragment)

    }

    private fun moveToLogin() {
        navGraph.setStartDestination(R.id.loginFragment)
        navController.graph = navGraph
        navController.navigate(R.id.loginFragment)

    }
}