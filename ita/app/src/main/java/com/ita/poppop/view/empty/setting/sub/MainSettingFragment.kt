package com.ita.poppop.view.empty.setting.sub

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ita.poppop.R
import com.ita.poppop.data.remote.api.SignupRequest
import com.ita.poppop.data.remote.repository.Member.MemberRepository
import com.ita.poppop.data.remote.repository.Member.MemberRepositoryImpl
import com.ita.poppop.util.RetrofitClient
import com.ita.poppop.util.dialog.LogoutDialog
import com.ita.poppop.util.dialog.WithdrawDialog
import com.ita.poppop.view.empty.setting.SettingFragment
import com.ita.poppop.viewmodel.MainAViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MainSettingFragment : PreferenceFragmentCompat() {
    private lateinit var mainAViewModel: MainAViewModel
    private val repository: MemberRepository = MemberRepositoryImpl(RetrofitClient.memberApi)
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
                lifecycleScope.launch {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            repository.postLogout(mainAViewModel.tokenPair.value?.first!!)
                        }

                        if (result.isSuccessful) {
                            mainAViewModel.logout()
                        }
                    } catch (e: HttpException) {
                        // HTTP 에러 상세 정보
                        Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Exception: ${e.message}", e)
                    }
                }

            }
        })
        dialog.show()
    }

    private fun showWithdrawDialog() {
        val dialog = WithdrawDialog(requireContext())
        dialog.setItemClickListener(object : WithdrawDialog.ItemClickListener {
            override fun onClick() {
                mainAViewModel.unlink()
                //TODO 서버 사용자 DB애서 사용자 제거 후 프리퍼런스에 서버에서 받아온 토큰 저장

                /**
                 *  lifecycleScope.launch {
                 *      try {
                 *          val result = withContext(Dispatchers.IO) {
                 *              repository.postLogout(mainAViewModel.tokenPair.value?.first!!)
                 *          }
                 *          if (result.isSuccessful) {
                 *              mainAViewModel.logout()
                 *          }
                 *      } catch (e: HttpException) {
                 *          // HTTP 에러 상세 정보
                 *          Log.e("API_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
                 *      } catch (e: Exception) {
                 *          Log.e("API_ERROR", "Exception: ${e.message}", e)
                 *      }
                 *  }
                 */
            }
        })
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (parentFragment as? SettingFragment)?.updateToolbarTitle("설정")
    }
}
