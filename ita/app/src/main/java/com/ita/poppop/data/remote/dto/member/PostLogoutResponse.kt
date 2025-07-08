package com.ita.poppop.data.remote.dto.member

import com.google.gson.annotations.SerializedName

data class PostLogoutResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: Any? = null
)