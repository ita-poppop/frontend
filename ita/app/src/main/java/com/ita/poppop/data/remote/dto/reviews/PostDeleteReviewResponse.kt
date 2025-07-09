package com.ita.poppop.data.remote.dto.reviews

import com.google.gson.annotations.SerializedName

data class PostDeleteReviewResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: Any
)