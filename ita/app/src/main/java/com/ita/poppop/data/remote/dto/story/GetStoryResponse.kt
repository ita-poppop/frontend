package com.ita.poppop.data.remote.dto.story

import com.google.gson.annotations.SerializedName


data class GetStoryResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<StoryData>?
)

data class StoryData(
    @SerializedName("storyId")
    val storyId: Int,

    @SerializedName("photoUrl")
    val photoUrl: String,

    @SerializedName("writerName")
    val writerName: String,

    @SerializedName("profileUrl")
    val profileUrl: String,

    @SerializedName("isRead")
    val isRead: Boolean,

    @SerializedName("createdAt")
    val createdAt: String
)