package com.ita.poppop.data.remote.dto.story

import com.google.gson.annotations.SerializedName

data class PostDeleteStoryResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: Any?
)
