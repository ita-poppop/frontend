package com.ita.poppop.view.empty.info.review.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.dto.ReviewData
import com.ita.poppop.data.remote.repository.popup.ReviewRepository
import com.ita.poppop.util.ConvertTimeUtil
import com.ita.poppop.view.empty.info.review.InfoReviewRVItem
import com.ita.poppop.view.empty.info.review.image.InfoReviewImageRVItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoReviewDetailViewModel(
    private val repository: ReviewRepository
) : ViewModel() {
    private val _inforeviewdetailList = MutableLiveData<InfoReviewRVItem>()
    val review: LiveData<InfoReviewRVItem> = _inforeviewdetailList

    private val _heartCount = MutableLiveData<Int>()
    val heartCount: LiveData<Int> = _heartCount

    private val _isHeartClicked = MutableLiveData<Boolean>()
    val isHeartClicked: LiveData<Boolean> = _isHeartClicked

    fun firstHeartCount(count: Int, clicked: Boolean = false) {
        if (_heartCount.value == null) {
            _heartCount.value = count
            _isHeartClicked.value = clicked
        }
    }

    fun clickHeart() {
        val currentClicked = _isHeartClicked.value ?: false
        val currentCount = _heartCount.value ?: 0
        if (currentClicked) {
            _heartCount.value = currentCount - 1
            _isHeartClicked.value = false
        } else {
            _heartCount.value = currentCount + 1
            _isHeartClicked.value = true
        }
    }

    fun getInfoReviewDetail(reviewId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getReview(1325, reviewId)
                }
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val data: ReviewData = responseBody.data
                        val result = reviewDtoToAdapterItem(data)
                        _inforeviewdetailList.value = result
                        Log.d("ReviewDetailApi_SUCCESS", "Review: $result")
                    }
                } else {
                    Log.e(
                        "ReviewDetailApi_ERROR",
                        "API error: ${response.message()} (${response.code()})"
                    )
                }
            } catch (e: Exception) {
                Log.e("ReviewDetailApi_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    // 데이터 변환
    private fun reviewDtoToAdapterItem(data: ReviewData): InfoReviewRVItem {
        val profileImage = "R.drawable._profile_load_icon" // 기본 이미지

        val convertTimeUtil = ConvertTimeUtil()

        val relativeTime = if (data.createdAt != data.updatedAt) {
            "${convertTimeUtil.convertRelativeTime(data.createdAt)} (수정됨)"
        } else {
            convertTimeUtil.convertRelativeTime(data.createdAt)
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
}