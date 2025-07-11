package com.ita.poppop.data.remote.dto.reviews

data class GetReviewResponse(
    val code: String,
    val data: ReviewData
)

data class ReviewData(
    val reviewId: Int,
    val content: String,
    val imageUrls: List<String>,
    val writerProfileUrl: String,
    val writerName: String,
    val createdAt: String,
    val updatedAt: String,
    val likeCount: Int,
    val commentCount: Int,
    val likedByUser: Boolean
)