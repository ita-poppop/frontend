package com.ita.poppop.data.remote.dto.comments

data class DeleteCommentResponse(
    val code: String,
    val message: String,
    val data: CommentData?
)
