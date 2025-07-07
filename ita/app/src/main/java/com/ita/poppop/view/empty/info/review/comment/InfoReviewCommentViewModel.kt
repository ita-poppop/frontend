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
    private val repository: CommentRepository
) : ViewModel() {
    private val _inforeviewcommentList = MutableLiveData<MutableList<InfoReviewCommentRVItem>>()
    val inforeviewcommentList: LiveData<MutableList<InfoReviewCommentRVItem>> = _inforeviewcommentList

    // 리뷰 상세 화면에서 댓글 추가
    fun postComment(reviewId: Int, content: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsnoTspIDtmJUiLCJpZCI6IjQzMjQ0NzEzMTQiLCJuaWNrTmFtZSI6IuyehOykgO2YlSIsImVtYWlsIjoibGltanVuaHllbmdAZ21haWwuY29tIiwiUHJvZmlsZUltYWdlIjoiaHR0cHM6Ly9pbWcxLmtha2FvY2RuLm5ldC90aHVtYi9SNjQweDY0MC5xNzAvP2ZuYW1lPWh0dHBzOi8vdDEua2FrYW9jZG4ubmV0L2FjY291bnRfaW1hZ2VzL2RlZmF1bHRfcHJvZmlsZS5qcGVnIiwiaWF0IjoxNzUxODY3NDYyfQ.84sTRKySacPuKTTgZZXM0SUmxPD_8ujFobzpDx0HUSU"
                    val request = PostCommentRequest(content, parentId = 0)
                    repository.postComment(accessToken, reviewId, request)
                }
                if (response.isSuccessful) {
                    response.body()?.data?.let { commentData  ->
                        val newComment = commentDtoToAdapterItem(commentData)
                        val currentList = _inforeviewcommentList.value ?: mutableListOf()
                        val updatedList = currentList.toMutableList()
                        updatedList.add(newComment)
                        _inforeviewcommentList.value = updatedList
                        Log.d("CommentApi_SUCCESS1", "comment: $updatedList")
                    }
                } else {
                    Log.e("CommentApi_ERROR1", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("CommentApi_ERROR1", "Exception: ${e.message}", e)
            }
        }

        /*val currentList = _inforeviewcommentList.value ?: mutableListOf()
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
        _inforeviewcommentList.value = updatedList*/

    }
    
    // 리뷰 상세 화면에서 댓글 삭제
    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsnoTspIDtmJUiLCJpZCI6IjQzMjQ0NzEzMTQiLCJuaWNrTmFtZSI6IuyehOykgO2YlSIsImVtYWlsIjoibGltanVuaHllbmdAZ21haWwuY29tIiwiUHJvZmlsZUltYWdlIjoiaHR0cHM6Ly9pbWcxLmtha2FvY2RuLm5ldC90aHVtYi9SNjQweDY0MC5xNzAvP2ZuYW1lPWh0dHBzOi8vdDEua2FrYW9jZG4ubmV0L2FjY291bnRfaW1hZ2VzL2RlZmF1bHRfcHJvZmlsZS5qcGVnIiwiaWF0IjoxNzUxODY3NDYyfQ.84sTRKySacPuKTTgZZXM0SUmxPD_8ujFobzpDx0HUSU"
                    repository.deleteComment(accessToken, commentId)
                }
                if (response.isSuccessful) {
                    response.body()?.let { commentData  ->
                        val currentList = _inforeviewcommentList.value ?: mutableListOf()
                        val updatedList = currentList.filterNot { it.itemId == commentId }.toMutableList()
                        _inforeviewcommentList.value = updatedList
                        Log.d("CommentApi_SUCCESS2", "deletecomment: $updatedList")
                    }
                } else {
                    Log.e("CommentApi_ERROR2", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("CommentApi_ERROR2", "Exception: ${e.message}", e)
            }
        }
        /*val currentList = _inforeviewcommentList.value ?: return
        Log.d("DeleteComment", "Deleting id: $commentItemId")
        Log.d("DeleteComment", "Before delete: ${currentList.map { it.itemId }}")
        val updatedList = currentList.filterNot { it.itemId == commentItemId }.toMutableList()
        Log.d("DeleteComment", "After delete: ${updatedList.map { it.itemId }}")
        _inforeviewcommentList.value = updatedList*/
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

        val convertTimeUtil = ConvertTimeUtil()

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
            reply = data.children.size,
            content = data.content
        )
    }

    // 데이터 변환
    private fun commentListDtoToAdapterItem(data: CommentListData): InfoReviewCommentRVItem {
        val convertTimeUtil = ConvertTimeUtil()

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
            reply = data.replyCount,
            content = data.content
        )
    }


}
