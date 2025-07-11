package com.ita.poppop.data.remote.dto.reviews

data class DeleteReviewResponse(
    val code: String,
    val message: String,
    val data: ReviewData
)
