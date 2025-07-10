package com.ita.poppop.data.remote.dto.profile

import com.google.gson.annotations.SerializedName

data class GetProfileResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UserReviewData
)

data class UserReviewData(
    @SerializedName("profileUrl")
    val profileUrl: String,

    @SerializedName("userName")
    val userName: String,

    @SerializedName("reviewCount")
    val reviewCount: Int,

    @SerializedName("totalLikeCount")
    val totalLikeCount: Int,

    @SerializedName("reviews")
    val reviews: List<ReviewItem>
)

data class ReviewItem(
    @SerializedName("reviewId")
    val reviewId: Int,

    @SerializedName("popupImageUrl")
    val popupImageUrl: String,

    @SerializedName("popupTitle")
    val popupTitle: String,

    @SerializedName("startDate")
    val startDate: String,
    
    @SerializedName("endDate")
    val endDate: String
)
