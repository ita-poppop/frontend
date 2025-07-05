package com.ita.poppop.view.empty.info.review.comment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.dto.CommentListData
import com.ita.poppop.data.remote.repository.popup.CommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class InfoReviewCommentViewModel(
    private val repository: CommentRepository
) : ViewModel() {
    private val _inforeviewcommentList = MutableLiveData<MutableList<InfoReviewCommentRVItem>>()
    val inforeviewcommentList: LiveData<MutableList<InfoReviewCommentRVItem>> = _inforeviewcommentList

    // 리뷰 상세 화면에서 댓글 추가
    fun addComment(content: String) {
        val currentList = _inforeviewcommentList.value ?: mutableListOf()
        val newId = (currentList.maxOfOrNull { it.itemId } ?: 0) + 1
        val newComment = InfoReviewCommentRVItem(
            itemId = newId,
            username = "hello",
            profileImage = "R.drawable._profile_load_icon",
            content = content,
            time = "방금 전",
            reply = 0
        )
        val updatedList = currentList.toMutableList()
        updatedList.add(newComment)
        _inforeviewcommentList.value = updatedList

    }
    
    // 리뷰 상세 화면에서 댓글 삭제
    fun deleteComment(commentItemId: Int) {
        val currentList = _inforeviewcommentList.value ?: return
        Log.d("DeleteComment", "Deleting id: $commentItemId")
        Log.d("DeleteComment", "Before delete: ${currentList.map { it.itemId }}")
        val updatedList = currentList.filterNot { it.itemId == commentItemId }.toMutableList()
        Log.d("DeleteComment", "After delete: ${updatedList.map { it.itemId }}")
        _inforeviewcommentList.value = updatedList
    }

    fun getInfoReviewCommentList(reviewId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getCommentList(reviewId,1,5)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val commentItems = body.data.map { commentListDtoToAdapterItem(it) }.toMutableList()
                        _inforeviewcommentList.value = commentItems
                        Log.d("CommentApi_SUCCESS", "ReviewList: $commentItems")
                    }
                } else {
                    Log.e("CommentApi_ERROR", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("CommentApi_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    // 데이터 변환
    private fun commentListDtoToAdapterItem(data: CommentListData): InfoReviewCommentRVItem {

        val relativeTime = if (data.createdAt != data.updatedAt) {
            "${convertTimeString(data.createdAt)} (수정됨)"
        } else {
            convertTimeString(data.createdAt)
        }

        return InfoReviewCommentRVItem(
            itemId = data.commentId,
            profileImage = data.writerProfileUrl,
            username = data.writerName,
            time = relativeTime,
            reply = data.replyCount,
            content = data.content
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
