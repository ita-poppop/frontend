package com.ita.poppop.data.remote.dto.member

import com.google.gson.annotations.SerializedName


data class PostSignupResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UserData

)


data class UserData(
    @SerializedName("email")
    val email: String,

    @SerializedName("accessTocken")
    val accessTocken: String,

    @SerializedName("refreshTocken")
    val refreshTocken: String
)