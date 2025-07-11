package com.ita.poppop.view.empty.info.review.detail.reply

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.dto.comments.CommentData
import com.ita.poppop.data.remote.dto.comments.PostCommentRequest
import com.ita.poppop.data.remote.repository.popup.CommentRepository
import com.ita.poppop.util.ConvertTimeUtil
import com.ita.poppop.view.empty.info.review.InfoReviewDetailReplyRVItem
import com.ita.poppop.view.empty.info.review.comment.InfoReviewCommentRVItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoReviewDetailReplyViewModel(
    private val accessToken: String,
    private val repository: CommentRepository
) : ViewModel(){
    private val _infocommentdetail = MutableLiveData<InfoReviewCommentRVItem>()
    val infocommentdetail: LiveData<InfoReviewCommentRVItem> = _infocommentdetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _inforeviewdetailreplyList =
        MutableLiveData<MutableList<InfoReviewDetailReplyRVItem>>()
    val inforeviewdetailreplyList: LiveData<MutableList<InfoReviewDetailReplyRVItem>> =
        _inforeviewdetailreplyList

    private val convertTimeUtil = ConvertTimeUtil()

    // 댓글 화면에서 대댓글 추가
    fun postReply(reviewId: Int, reply: String, parentId: Int? = null, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val request = PostCommentRequest(reply, parentId)
                    repository.postComment(accessToken, reviewId, request)
                }
                if (response.isSuccessful) {
                    response.body()?.data?.let { commentData  ->
                        val newReply = replyDtoToAdapterItem(commentData)
                        val currentList = _inforeviewdetailreplyList.value ?: mutableListOf()
                        val updatedList = currentList.toMutableList()
                        updatedList.add(newReply)
                        _inforeviewdetailreplyList.postValue(updatedList)
                        Log.d("ReplyApi_SUCCESS", "Replies: $updatedList")
                    }
                    onSuccess?.invoke()
                } else {
                    Log.e("ReplyApi_ERROR", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("ReplyApi_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    // 댓글 화면에서 대댓글 삭제
    fun deleteReply(replyId: Int, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.deleteComment(accessToken, replyId)
                }
                if (response.isSuccessful) {
                    val currentList = _inforeviewdetailreplyList.value ?: mutableListOf()
                    val updatedList = currentList.filterNot { it.itemId == replyId }.toMutableList()
                    _inforeviewdetailreplyList.value = updatedList
                    Log.d("ReplyApi_SUCCESS_Delete", "Deleted replyId: $replyId")
                    onSuccess?.invoke()
                } else {
                    Log.e("ReplyApi_ERROR_Delete", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("ReplyApi_ERROR_Delete", "Exception: ${e.message}", e)
            }
        }
    }

    fun getInfoCommentDetail(reviewId: Int, commentId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
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

                        val currentUserName = getUserNameFromToken()
                        val replies = data.children.map {
                            InfoReviewDetailReplyRVItem(
                                itemId = it.commentId,
                                username = it.writerName,
                                profileImage = it.writerProfileUrl?: "",
                                reply = it.content,
                                time = convertTimeUtil.convertRelativeTime(it.createdAt),
                                isMine = it.writerName.equals(currentUserName, ignoreCase = true)
                            )
                        }.toMutableList()

                        _inforeviewdetailreplyList.value = replies
                    }
                } else {
                    Log.e("CommentDetailApi_ERROR", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("CommentDetailApi_ERROR", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 데이터 변환
    private fun replyDtoToAdapterItem(data: CommentData): InfoReviewDetailReplyRVItem {

        val convertTimeUtil = ConvertTimeUtil().convertRelativeTime(data.createdAt)

        return InfoReviewDetailReplyRVItem (
            itemId = data.commentId,
            profileImage = data.writerProfileUrl ?: "",
            username = data.writerName,
            time = convertTimeUtil,
            reply = data.content
        )
    }

    // 데이터 변환
    private fun commentDtoToAdapterItem(data: CommentData): InfoReviewCommentRVItem {

        val convertTimeUtil = ConvertTimeUtil().convertRelativeTime(data.createdAt)

        return InfoReviewCommentRVItem(
            itemId = data.commentId,
            profileImage = data.writerProfileUrl?: "",
            username = data.writerName,
            time = convertTimeUtil,
            content = data.content,
            reply = data.children.size,
            isMine = false
        )
    }

    fun getUserNameFromToken(): String? {
        val token = accessToken
        val parts = token.split(".")
        if (parts.size < 2) return null
        return try {
            val payloadJson = String(android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT))
            val jsonObj = org.json.JSONObject(payloadJson)
            val name = jsonObj.optString("sub").takeIf { it.isNotEmpty() }
                ?: jsonObj.optString("nickName").takeIf { it.isNotEmpty() }
            name
        } catch (e: Exception) {
            null
        }
    }
}