package com.ita.poppop.view.empty.info.review

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.dto.reviews.ReviewListData
import com.ita.poppop.data.remote.repository.popup.ReviewRepository
import com.ita.poppop.util.ConvertTimeUtil
import com.ita.poppop.view.empty.info.review.image.InfoReviewImageRVItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoReviewViewModel(
    private val repository: ReviewRepository
) : ViewModel() {
    private val _inforeviewList = MutableLiveData<MutableList<InfoReviewRVItem>>()
    val inforeviewList: LiveData<MutableList<InfoReviewRVItem>> = _inforeviewList

    fun getInfoReview() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getReviewList(1325, 1, 20)
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
