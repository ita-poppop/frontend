package com.ita.poppop.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager

object NotificationSettingsManager {

    // 알림 채널 ID들
    const val POPUP_OPEN_CHANNEL_ID = "poppop_popup_open_channel"
    const val REVIEW_LIKE_CHANNEL_ID = "poppop_review_like_channel"
    const val COMMENT_ALARM_CHANNEL_ID = "poppop_comment_alarm_channel"

    // 알림 채널 이름들
    const val POPUP_OPEN_CHANNEL_NAME = "팝업 오픈 알림"
    const val REVIEW_LIKE_CHANNEL_NAME = "리뷰 좋아요 알림"
    const val COMMENT_ALARM_CHANNEL_NAME = "댓글 알람"

    // 알림 채널 설명들
    const val POPUP_OPEN_CHANNEL_DESCRIPTION = "새로운 팝업 스토어 오픈 알림"
    const val REVIEW_LIKE_CHANNEL_DESCRIPTION = "리뷰 좋아요 알림"
    const val COMMENT_ALARM_CHANNEL_DESCRIPTION = "댓글 관련 알림"

    /**
     * 앱 시작 시 저장된 알림 설정을 읽어와서 적용
     */
    fun applyStoredSettings(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val isPopupOpenEnabled = sharedPreferences.getBoolean("set_1", true)
        val isReviewLikeEnabled = sharedPreferences.getBoolean("set_2", true)
        val isCommentAlarmEnabled = sharedPreferences.getBoolean("set_3", true)

        applyPopupOpenSettings(context, isPopupOpenEnabled)
        applyReviewLikeSettings(context, isReviewLikeEnabled)
        applyCommentAlarmSettings(context, isCommentAlarmEnabled)
    }

    /**
     * 팝업 오픈 알림 설정 적용
     */
    fun applyPopupOpenSettings(context: Context, isEnabled: Boolean) {
        if (isEnabled) {
            createOrUpdateNotificationChannel(
                context = context,
                channelId = POPUP_OPEN_CHANNEL_ID,
                channelName = POPUP_OPEN_CHANNEL_NAME,
                channelDescription = POPUP_OPEN_CHANNEL_DESCRIPTION,
                importance = NotificationManager.IMPORTANCE_HIGH
            )
        } else {
            disableNotificationChannel(context, POPUP_OPEN_CHANNEL_ID)
        }

        android.util.Log.d("NotificationSettings", "팝업 오픈 알림 설정: $isEnabled")
    }

    /**
     * 리뷰 좋아요 알림 설정 적용
     */
    fun applyReviewLikeSettings(context: Context, isEnabled: Boolean) {
        if (isEnabled) {
            createOrUpdateNotificationChannel(
                context = context,
                channelId = REVIEW_LIKE_CHANNEL_ID,
                channelName = REVIEW_LIKE_CHANNEL_NAME,
                channelDescription = REVIEW_LIKE_CHANNEL_DESCRIPTION,
                importance = NotificationManager.IMPORTANCE_DEFAULT
            )
        } else {
            disableNotificationChannel(context, REVIEW_LIKE_CHANNEL_ID)
        }

        android.util.Log.d("NotificationSettings", "리뷰 좋아요 알림 설정: $isEnabled")
    }

    /**
     * 댓글 알람 설정 적용
     */
    fun applyCommentAlarmSettings(context: Context, isEnabled: Boolean) {
        if (isEnabled) {
            createOrUpdateNotificationChannel(
                context = context,
                channelId = COMMENT_ALARM_CHANNEL_ID,
                channelName = COMMENT_ALARM_CHANNEL_NAME,
                channelDescription = COMMENT_ALARM_CHANNEL_DESCRIPTION,
                importance = NotificationManager.IMPORTANCE_DEFAULT
            )
        } else {
            disableNotificationChannel(context, COMMENT_ALARM_CHANNEL_ID)
        }

        android.util.Log.d("NotificationSettings", "댓글 알람 설정: $isEnabled")
    }

    /**
     * 알림 채널 생성 또는 업데이트
     */
    private fun createOrUpdateNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String,
        channelDescription: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription

                // 소리 설정
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)

                // 진동 설정
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)

                // LED 설정
                enableLights(true)
                lightColor = android.graphics.Color.BLUE

                // 잠금 화면 표시 설정
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC

                // 배지 설정
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 특정 알림 채널 비활성화
     */
    private fun disableNotificationChannel(context: Context, channelId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 해당 채널의 기존 알림 모두 취소
        notificationManager.cancel(channelId.hashCode())

        // Android O 이상에서는 채널 중요도를 NONE으로 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            existingChannel?.let { channel ->
                val disabledChannel = NotificationChannel(
                    channelId,
                    "${channel.name} (비활성화됨)",
                    NotificationManager.IMPORTANCE_NONE
                ).apply {
                    description = "${channel.description} (비활성화됨)"
                    enableVibration(false)
                    enableLights(false)
                    setShowBadge(false)
                }
                notificationManager.createNotificationChannel(disabledChannel)
            }
        }
    }

    /**
     * 모든 알림 비활성화
     */
    fun disableAllNotifications(context: Context) {
        disableNotificationChannel(context, POPUP_OPEN_CHANNEL_ID)
        disableNotificationChannel(context, REVIEW_LIKE_CHANNEL_ID)
        disableNotificationChannel(context, COMMENT_ALARM_CHANNEL_ID)
    }

    /**
     * 배지 카운트 초기화
     */
    fun clearBadgeCount(context: Context) {
        try {
            // 일부 제조사별 배지 초기화 방법
            val intent = android.content.Intent("android.intent.action.BADGE_COUNT_UPDATE")
            intent.putExtra("badge_count", 0)
            intent.putExtra("badge_count_package_name", context.packageName)
            intent.putExtra("badge_count_class_name", "${context.packageName}.MainActivity")
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            android.util.Log.w("NotificationSettings", "배지 초기화 실패: ${e.message}")
        }
    }

    /**
     * 특정 알림 타입이 활성화되어 있는지 확인
     */
    fun isNotificationTypeEnabled(context: Context, notificationType: String): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return when (notificationType) {
            "popup_open" -> sharedPreferences.getBoolean("set_1", true)
            "review_like" -> sharedPreferences.getBoolean("set_2", true)
            "comment_alarm" -> sharedPreferences.getBoolean("set_3", true)
            else -> false
        }
    }

    /**
     * 알림 권한 확인
     */
    fun isNotificationPermissionGranted(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    /**
     * 특정 채널의 알림 권한 확인
     */
    fun isChannelEnabled(context: Context, channelId: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = notificationManager.getNotificationChannel(channelId)
            return channel?.importance != NotificationManager.IMPORTANCE_NONE
        }
        return isNotificationPermissionGranted(context)
    }

    /**
     * 알림 설정 페이지로 이동
     */
    fun openNotificationSettings(context: Context) {
        val intent = android.content.Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
            } else {
                action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = android.net.Uri.parse("package:${context.packageName}")
            }
        }
        context.startActivity(intent)
    }

    /**
     * 특정 알림 채널 설정 페이지로 이동 (Android O 이상)
     */
    fun openChannelSettings(context: Context, channelId: String = POPUP_OPEN_CHANNEL_ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = android.content.Intent(android.provider.Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                putExtra(android.provider.Settings.EXTRA_CHANNEL_ID, channelId)
            }
            context.startActivity(intent)
        }
    }

    /**
     * 기본 채널 설정 페이지로 이동
     */
    fun openChannelSettings(context: Context) {
        openChannelSettings(context, POPUP_OPEN_CHANNEL_ID)
    }

    /**
     * 팝업 오픈 알림 전송
     */
    fun sendPopupOpenNotification(context: Context, popupName: String, location: String) {
        if (!isNotificationTypeEnabled(context, "popup_open")) return

        // TODO: 실제 알림 전송 로직 구현
        android.util.Log.d("NotificationSettings", "팝업 오픈 알림 전송: $popupName at $location")
    }

    /**
     * 리뷰 좋아요 알림 전송
     */
    fun sendReviewLikeNotification(context: Context, reviewTitle: String, likeCount: Int) {
        if (!isNotificationTypeEnabled(context, "review_like")) return

        // TODO: 실제 알림 전송 로직 구현
        android.util.Log.d("NotificationSettings", "리뷰 좋아요 알림 전송: $reviewTitle - $likeCount likes")
    }

    /**
     * 댓글 알람 전송
     */
    fun sendCommentAlarmNotification(context: Context, commentContent: String, authorName: String) {
        if (!isNotificationTypeEnabled(context, "comment_alarm")) return

        // TODO: 실제 알림 전송 로직 구현
        android.util.Log.d("NotificationSettings", "댓글 알람 전송: $commentContent by $authorName")
    }
}