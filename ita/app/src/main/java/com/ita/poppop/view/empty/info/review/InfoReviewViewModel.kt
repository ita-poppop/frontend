package com.ita.poppop.view.empty.info.review

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.dto.ReviewListData
import com.ita.poppop.data.remote.repository.popup.ReviewRepository
import com.ita.poppop.view.empty.info.review.image.InfoReviewImageRVItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class InfoReviewViewModel(
    private val repository: ReviewRepository
) : ViewModel() {
    private val _inforeviewList = MutableLiveData<MutableList<InfoReviewRVItem>>()
    val inforeviewList: LiveData<MutableList<InfoReviewRVItem>> = _inforeviewList

    fun getInfoReview() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getReviewList(1325, 1, 5)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val reviewItems = body.data.map { reviewListDtoToAdapterItem(it) }.toMutableList()
                        _inforeviewList.value = reviewItems
                        Log.d("ReviewApi_SUCCESS", "ReviewList: $reviewItems")
                    }
                } else {
                    Log.e("ReviewApi_ERROR", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("ReviewApi_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    // 데이터 변환
    private fun reviewListDtoToAdapterItem(data: ReviewListData): InfoReviewRVItem {
        val profileImage = "R.drawable._profile_load_icon" // 기본 이미지

        val relativeTime = if (data.createdAt != data.updatedAt) {
            "${convertTimeString(data.createdAt)} (수정됨)"
        } else {
            convertTimeString(data.createdAt)
        }

        val reviewImages = data.imageUrls.mapIndexed { index, url ->
            InfoReviewImageRVItem(index, url)
        }

        return InfoReviewRVItem(
            itemId = data.reviewId,
            profileImage = profileImage, // API X -> 일단 기본이미지로
            username = data.writerName,
            time = relativeTime,
            hearts = data.likeCount,
            comments = data.commentCount,
            content = data.content,
            reviewImage = reviewImages
        )
    }
    
    // 날짜 데이터 변환
    private fun convertTimeString(isoString: String): String {
        try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val createdTime = LocalDateTime.parse(isoString, formatter)
            val now = LocalDateTime.now(ZoneId.systemDefault())

            val minutes = ChronoUnit.MINUTES.between(createdTime, now)
            if (minutes < 1) return "방금 전"
            if (minutes < 60) return "${minutes}분 전"

            val hours = ChronoUnit.HOURS.between(createdTime, now)
            if (hours < 24) return "${hours}시간 전"

            val days = ChronoUnit.DAYS.between(createdTime, now)
            if (days < 7) return "${days}일 전"

            val weeks = ChronoUnit.WEEKS.between(createdTime, now)
            if (weeks < 4) return "${weeks}주 전"

            val months = ChronoUnit.MONTHS.between(createdTime, now)
            if (months < 12) return "${months}개월 전"

            // 1년 이상 경과 -> 날짜 출력
            return createdTime.format(DateTimeFormatter.ofPattern("MM/dd"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}
