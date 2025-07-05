package com.ita.poppop.view.empty.setting.sub

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ita.poppop.R
import com.ita.poppop.util.dialog.LogoutDialog
import com.ita.poppop.util.dialog.WithdrawDialog
import com.ita.poppop.view.empty.setting.SettingFragment
import com.ita.poppop.viewmodel.MainAViewModel

class MainSettingFragment : PreferenceFragmentCompat() {
    private lateinit var mainAViewModel: MainAViewModel
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
        setupPreferenceClickListeners()


        mainAViewModel = ViewModelProvider(requireActivity())[MainAViewModel::class.java]
    }

    private fun setupPreferenceClickListeners() {
        setClickListener("pp") { openTermsOfService() }
        setClickListener("set_noti") { openNotificationSettings() }
        setClickListener("logout") { showLogoutDialog() }
        setClickListener("withdraw") { showWithdrawDialog() }
    }

    private fun setClickListener(key: String, action: () -> Unit) {
        findPreference<Preference>(key)?.setOnPreferenceClickListener {
            action()
            true
        }
    }

    private fun openTermsOfService() {
        // TODO: WebView 또는 약관 프래그먼트로 이동
    }

    private fun openNotificationSettings() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_setting, NotificationSettingFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showLogoutDialog() {
        val dialog = LogoutDialog(requireContext())
        dialog.setItemClickListener(object : LogoutDialog.ItemClickListener {
            override fun onClick() {
                mainAViewModel.logout()
            }
        })
        dialog.show()
    }

    private fun showWithdrawDialog() {
        val dialog = WithdrawDialog(requireContext())
        dialog.setItemClickListener(object : WithdrawDialog.ItemClickListener {
            override fun onClick() {
                mainAViewModel.unlink()
                //TODO 서버 사용자 DB애서 사용자 제거
            }
        })
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (parentFragment as? SettingFragment)?.updateToolbarTitle("설정")
    }
}
