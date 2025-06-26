package com.ita.poppop.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ita.poppop.R
import com.ita.poppop.base.BaseFragment
import com.ita.poppop.databinding.FragmentMainBinding

class MainFragment: BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {
    override fun initView() {
        binding.apply {
            // FragmentContainerView에 동적으로 navi 연결
            val navHostFragment = childFragmentManager
                .findFragmentById(R.id.fcv_main_fragment_container) as NavHostFragment
            val navController = navHostFragment.navController
            navController.setGraph(R.navigation.navi_main)

            // BottomNavigationView와 navi 연결
            bnvMainNavi.setupWithNavController(navController)


        }

    }
}