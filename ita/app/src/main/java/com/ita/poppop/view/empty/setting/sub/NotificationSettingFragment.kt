package com.ita.poppop.view.empty.setting.sub

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.ita.poppop.R
import com.ita.poppop.view.empty.setting.SettingFragment
import com.ita.poppop.util.NotificationSettingsManager
import com.ita.poppop.util.preference.EndSwitchPreference

class NotificationSettingFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_notification, rootKey)

        // SharedPreferences 초기화
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateToolbarTitle("알림 설정")
        setupPreferenceClickListeners()
        // loadPreferenceStates() 제거 - EndSwitchPreference가 자동으로 처리
        checkNotificationPermission()
        applyAllSettings() // 한 번만 호출
    }

    override fun onResume() {
        super.onResume()
        // SharedPreferences 변경 리스너 등록
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        // SharedPreferences 변경 리스너 해제
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateToolbarTitle("설정")
    }

    private fun updateToolbarTitle(title: String) {
        (parentFragment as? SettingFragment)?.updateToolbarTitle(title)
    }

    private fun setupPreferenceClickListeners() {
        // 알림 권한 설정 페이지로 이동
        setClickListener("notification_permission") {
            NotificationSettingsManager.openNotificationSettings(requireContext())
        }

        // 알림 채널 설정 페이지로 이동
        setClickListener("notification_channel") {
            NotificationSettingsManager.openChannelSettings(requireContext())
        }

        // 일반 클릭 리스너 (페이지 이동 등)
        setClickListener("notification_detail") { navigateToNotificationDetail() }

        // EndSwitchPreference 설정
        setupEndSwitchPreference("set_1", "팝업 오픈")
        setupEndSwitchPreference("set_2", "리뷰 좋아요")
        setupEndSwitchPreference("set_3", "댓글 알람")
    }

    private fun setClickListener(key: String, action: () -> Unit) {
        findPreference<Preference>(key)?.setOnPreferenceClickListener {
            action()
            true
        }
    }

    private fun setupEndSwitchPreference(key: String, title: String) {
        findPreference<EndSwitchPreference>(key)?.apply {
            this.title = title
            // 기본값 설정
            setDefaultValue(true)
            // 스위치 상태 변경 리스너 설정
            setOnPreferenceChangeListener { _, newValue ->
                val isEnabled = newValue as Boolean
                handleSwitchChange(key, isEnabled)
                true
            }
        }
    }

    private fun handleSwitchChange(key: String, isEnabled: Boolean) {
        // 각 스위치별 처리 로직
        when (key) {
            "set_1" -> {
                // 팝업 오픈 설정 변경
                Log.d("NotificationSetting", "팝업 오픈 설정 변경: $isEnabled")
                applyPopupOpenSettings(isEnabled)
            }
            "set_2" -> {
                // 리뷰 좋아요 설정 변경
                Log.d("NotificationSetting", "리뷰 좋아요 설정 변경: $isEnabled")
                applyReviewLikeSettings(isEnabled)
            }
            "set_3" -> {
                // 댓글 알람 설정 변경
                Log.d("NotificationSetting", "댓글 알람 설정 변경: $isEnabled")
                applyCommentAlarmSettings(isEnabled)
            }
        }
    }


    private fun checkNotificationPermission() {
        val hasPermission = NotificationSettingsManager.isNotificationPermissionGranted(requireContext())

        // 권한 상태를 UI에 표시
        findPreference<Preference>("notification_permission")?.apply {
            summary = if (hasPermission) {
                "알림 권한이 허용되었습니다"
            } else {
                "알림 권한이 거부되었습니다. 탭하여 설정하세요"
            }
            isEnabled = !hasPermission
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "set_1" -> {
                val isEnabled = sharedPreferences?.getBoolean(key, true) ?: true
                applyPopupOpenSettings(isEnabled)
            }
            "set_2" -> {
                val isEnabled = sharedPreferences?.getBoolean(key, true) ?: true
                applyReviewLikeSettings(isEnabled)
            }
            "set_3" -> {
                val isEnabled = sharedPreferences?.getBoolean(key, true) ?: true
                applyCommentAlarmSettings(isEnabled)
            }
        }
    }

    private fun applyPopupOpenSettings(isEnabled: Boolean) {
        // 팝업 오픈 설정 적용
        NotificationSettingsManager.applyPopupOpenSettings(
            context = requireContext(),
            isEnabled = isEnabled
        )
    }

    private fun applyReviewLikeSettings(isEnabled: Boolean) {
        // 리뷰 좋아요 설정 적용
        NotificationSettingsManager.applyReviewLikeSettings(
            context = requireContext(),
            isEnabled = isEnabled
        )
    }

    private fun applyCommentAlarmSettings(isEnabled: Boolean) {
        // 댓글 알람 설정 적용
        NotificationSettingsManager.applyCommentAlarmSettings(
            context = requireContext(),
            isEnabled = isEnabled
        )
    }

    private fun applyAllSettings() {
        // 모든 설정을 한 번에 적용
        val isPopupOpenEnabled = sharedPreferences.getBoolean("set_1", true)
        val isReviewLikeEnabled = sharedPreferences.getBoolean("set_2", true)
        val isCommentAlarmEnabled = sharedPreferences.getBoolean("set_3", true)

        applyPopupOpenSettings(isPopupOpenEnabled)
        applyReviewLikeSettings(isReviewLikeEnabled)
        applyCommentAlarmSettings(isCommentAlarmEnabled)
    }

    private fun navigateToNotificationDetail() {
        // 알림 관련 세부 설정 페이지로 이동
        // TODO: WebView 또는 다른 Fragment로 이동 구현
    }
}