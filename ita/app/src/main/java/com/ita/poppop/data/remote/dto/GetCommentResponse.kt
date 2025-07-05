package com.ita.poppop.data.remote.dto

data class GetCommentResponse(
    val code: String,
    val data: CommentData
)

data class CommentData(
    val commentId: Int,
    val content: String,
    val writerProfileUrl: String,
    val writerName: String,
    val createdAt: String,
    val updatedAt: String,
    val children: List<CommentData>
)