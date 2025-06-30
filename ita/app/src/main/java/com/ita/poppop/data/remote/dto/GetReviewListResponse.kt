package com.ita.poppop.data.remote.dto

data class GetReviewListResponse(
    val code: String,
    val message: String,
    val data: List<ReviewListData>
)

data class ReviewListData(
    val reviewId: Int,
    val content: String,
    val imageUrls: List<String>,
    val writerName: String,
    val createdAt: String,
    val updatedAt: String
)