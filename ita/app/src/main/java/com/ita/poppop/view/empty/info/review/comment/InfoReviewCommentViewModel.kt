package com.ita.poppop.view.empty.info.review.comment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.dto.comments.CommentData
import com.ita.poppop.data.remote.dto.comments.CommentListData
import com.ita.poppop.data.remote.dto.comments.PostCommentRequest
import com.ita.poppop.data.remote.repository.popup.CommentRepository
import com.ita.poppop.util.ConvertTimeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoReviewCommentViewModel(
    private val accessToken: String,
    private val repository: CommentRepository
) : ViewModel() {
    private val _inforeviewcommentList = MutableLiveData<MutableList<InfoReviewCommentRVItem>>()
    val inforeviewcommentList: LiveData<MutableList<InfoReviewCommentRVItem>> = _inforeviewcommentList

    // 리뷰 상세 화면에서 댓글 추가
    fun postComment(reviewId: Int, content: String, parentId: Int? = null, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val request = PostCommentRequest(content, parentId)
                    repository.postComment(accessToken, reviewId, request)
                }
                if (response.isSuccessful) {
                    response.body()?.data?.let { commentData  ->
                        val newComment = commentDtoToAdapterItem(commentData)
                        val currentList = _inforeviewcommentList.value ?: mutableListOf()
                        val updatedList = currentList.toMutableList()
                        updatedList.add(newComment)
                        _inforeviewcommentList.postValue(updatedList)

                        Log.d("CommentApi_SUCCESS1", "comment: $updatedList")
                    }
                    getInfoReviewCommentList(reviewId)
                    onSuccess?.invoke()
                } else {
                    Log.e("CommentApi_ERROR1", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("CommentApi_ERROR1", "Exception: ${e.message}", e)
            }
        }
    }
    
    // 리뷰 상세 화면에서 댓글 삭제
    fun deleteComment(commentId: Int, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.deleteComment(accessToken, commentId)
                }
                if (response.isSuccessful) {
                    response.body()?.let { commentData  ->
                        val currentList = _inforeviewcommentList.value ?: mutableListOf()
                        val updatedList = currentList.filterNot { it.itemId == commentId }.toMutableList()
                        _inforeviewcommentList.value = updatedList
                        Log.d("CommentApi_SUCCESS2", "deletecomment: $updatedList")
                    }
                    onSuccess?.invoke()
                } else {
                    Log.e("CommentApi_ERROR2", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("CommentApi_ERROR2", "Exception: ${e.message}", e)
            }
        }
    }

    fun getInfoReviewCommentList(reviewId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getCommentList(reviewId,1,20)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val currentUserName = getUserNameFromToken() // 사용자 이름 파싱
                        val commentItems = body.data.map { data ->
                            val item = commentListDtoToAdapterItem(data)
                            Log.d("CompareUserName", "item.username=${item.username}, currentUserName=$currentUserName")
                            item.copy(isMine = item.username.equals(currentUserName, ignoreCase = true)) // 이름으로 비교
                        }.toMutableList()
                        _inforeviewcommentList.value = commentItems
                        Log.d("CommentApi_SUCCESS", "ReviewCommentList: $commentItems")
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
    private fun commentDtoToAdapterItem(data: CommentData): InfoReviewCommentRVItem {

        val convertTimeUtil = ConvertTimeUtil().convertRelativeTime(data.createdAt)

        return InfoReviewCommentRVItem(
            itemId = data.commentId,
            profileImage = data.writerProfileUrl ?: "",
            username = data.writerName,
            time = convertTimeUtil,
            reply = data.children.size,
            content = data.content
        )
    }

    // 데이터 변환
    private fun commentListDtoToAdapterItem(data: CommentListData): InfoReviewCommentRVItem {
        val convertTimeUtil = ConvertTimeUtil().convertRelativeTime(data.createdAt)

        return InfoReviewCommentRVItem(
            itemId = data.commentId,
            profileImage = data.writerProfileUrl ?: "",
            username = data.writerName,
            time = convertTimeUtil,
            reply = data.replyCount,
            content = data.content
        )
    }

    fun getUserNameFromToken(): String? {
        val token = accessToken
        val parts = token.split(".")
        if (parts.size < 2) return null
        return try {
            val payloadJson = String(android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT))
            Log.d("TokenPayload", "payloadJson: $payloadJson")  // 이걸로 payload 확인
            val jsonObj = org.json.JSONObject(payloadJson)
            val name = jsonObj.optString("sub").takeIf { it.isNotEmpty() }
                ?: jsonObj.optString("nickName").takeIf { it.isNotEmpty() }

            Log.d("TokenUserName", "userName: $name")
            name
        } catch (e: Exception) {
            Log.e("TokenUserName", "Error decoding token", e)
            null
        }
    }


}
