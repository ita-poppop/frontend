package com.ita.poppop.data.remote.dto.comments

data class PostCommentResponse(
    val code: String,
    val message: String,
    val data: CommentData?
)

data class PostCommentRequest(
    val content: String,
    val parentId: Int
)

