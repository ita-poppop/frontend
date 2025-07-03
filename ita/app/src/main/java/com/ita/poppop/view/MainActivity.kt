package com.ita.poppop.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.ita.poppop.R
import com.ita.poppop.base.BaseActivity
import com.ita.poppop.databinding.ActivityMainBinding
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch


class MainActivity: BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private lateinit var navGraph: NavGraph
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("checkStartFlow", "MainActivity")
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
            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { _, error ->
                    if (error != null) {
                        if (error is KakaoSdkError && error.isInvalidTokenError() == true) {
                            //로그인 필요
                            moveToLogin()
                        }
                        else {
                            //기타 에러
                            moveToLogin()
                        }
                    }
                    else {
                        //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                        moveToMain()
                    }
                }

            }
            else {
                //로그인 필요
                moveToLogin()
            }
//            val user = FirebaseAuth.getInstance().currentUser
//            if (user != null) {
//                // 자동 로그인 → 메인 액티비티
//                goToMain()
//            } else {
//                // 로그인 필요 → 로그인 액티비티
//                goToLogin()
//            }

        }
    }

    private fun moveToMain() {
        navGraph.setStartDestination(R.id.mainFragment)
        navController.graph = navGraph

    }

    private fun moveToLogin() {
        navGraph.setStartDestination(R.id.loginFragment)
        navController.graph = navGraph
    }
}