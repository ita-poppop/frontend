package com.ita.poppop.data.remote.dto.profile

import com.google.gson.annotations.SerializedName

data class PostProfileResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: Boolean
)