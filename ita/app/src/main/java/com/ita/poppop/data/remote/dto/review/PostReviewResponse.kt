package com.ita.poppop.data.remote.dto.review

import com.google.gson.annotations.SerializedName


data class PostReviewResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: Any
)

