package com.ita.poppop.view.empty.info.review.detail.reply

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.dto.comments.CommentData
import com.ita.poppop.data.remote.repository.popup.CommentRepository
import com.ita.poppop.util.ConvertTimeUtil
import com.ita.poppop.view.empty.info.review.InfoReviewDetailReplyRVItem
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentRVItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoReviewDetailReplyViewModel(
    private val repository: CommentRepository
) : ViewModel(){
    private val _infocommentdetail = MutableLiveData<InfoReviewCommentRVItem>()
    val infocommentdetail: LiveData<InfoReviewCommentRVItem> = _infocommentdetail

    private val _inforeviewdetailreplyList =
        MutableLiveData<MutableList<InfoReviewDetailReplyRVItem>>()
    val inforeviewdetailreplyList: LiveData<MutableList<InfoReviewDetailReplyRVItem>> =
        _inforeviewdetailreplyList

    private val convertTimeUtil = ConvertTimeUtil()

    // 댓글 화면에서 대댓글 추가
    fun addReply(reply: String) {
        val currentList = _inforeviewdetailreplyList.value ?: mutableListOf()
        val newId = (currentList.maxOfOrNull { it.itemId } ?: 0) + 1
        val newReply = InfoReviewDetailReplyRVItem(
            itemId = newId,
            username = "hello",
            profileImage = "R.drawable._profile_load_icon",
            reply = reply,
            time = "방금 전"
        )
        val updatedList = currentList.toMutableList()
        updatedList.add(newReply)
        _inforeviewdetailreplyList.value = updatedList

    }

    // 댓글 화면에서 대댓글 삭제
    fun deleteReply(replyItemId: Int) {
        val currentList = _inforeviewdetailreplyList.value ?: return
        val updatedList = currentList.filterNot { it.itemId == replyItemId }.toMutableList()
        _inforeviewdetailreplyList.value = updatedList
    }

    fun getInfoCommentDetail(reviewId: Int, commentId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getComment(reviewId, commentId)
                }
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val data: CommentData = responseBody.data
                        val result = commentDtoToAdapterItem(data)
                        _infocommentdetail.value = result
                        Log.d("CommentDetailApi_SUCCESS", "Review: $result")

                        val replies = data.children.map {
                            InfoReviewDetailReplyRVItem(
                                itemId = it.commentId,
                                username = it.writerName,
                                profileImage = it.writerProfileUrl,
                                reply = it.content,
                                time = convertTimeUtil.convertRelativeTime(it.createdAt)
                            )
                        }.toMutableList()

                        _inforeviewdetailreplyList.value = replies
                    }
                } else {
                    Log.e("CommentDetailApi_ERROR", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("CommentDetailApi_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    // 데이터 변환
    private fun commentDtoToAdapterItem(data: CommentData): InfoReviewCommentRVItem {

        val relativeTime = if (data.createdAt != data.updatedAt) {
            "${convertTimeUtil.convertRelativeTime(data.createdAt)} (수정됨)"
        } else {
            convertTimeUtil.convertRelativeTime(data.createdAt)
        }

        return InfoReviewCommentRVItem(
            itemId = data.commentId,
            profileImage = data.writerProfileUrl,
            username = data.writerName,
            time = relativeTime,
            content = data.content,
            reply = data.children.size
        )
    }
}