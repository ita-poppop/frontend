package com.ita.poppop.data.remote.dto.member

import com.google.gson.annotations.SerializedName


data class GetMemberInfoResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UserInfoData
)

data class UserInfoData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("providerId")
    val providerId: String,

    @SerializedName("registerId")
    val registerId: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("nickName")
    val nickName: String,

    @SerializedName("profileImage")
    val profileImage: String
)