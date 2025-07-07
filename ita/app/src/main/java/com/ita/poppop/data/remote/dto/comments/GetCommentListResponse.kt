package com.ita.poppop.data.remote.dto.comments

data class GetCommentListResponse(
    val code: String,
    val data: List<CommentListData>
)

data class CommentListData(
    val commentId: Int,
    val content: String,
    val writerProfileUrl: String,
    val writerName: String,
    val createdAt: String,
    val updatedAt: String,
    val replyCount: Int
)