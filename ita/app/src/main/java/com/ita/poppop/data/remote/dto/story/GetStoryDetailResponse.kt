package com.ita.poppop.data.remote.dto.story

import com.google.gson.annotations.SerializedName

data class GetStoryDetailResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: StoryDetailData?
)

data class StoryDetailData(
    @SerializedName("storyId")
    val storyId: Int,

    @SerializedName("photoUrl")
    val photoUrl: String,

    @SerializedName("writerName")
    val writerName: String,

    @SerializedName("writerProfileUrl")
    val writerProfileUrl: String,

    @SerializedName("popupTitle")
    val popupTitle: String,

    @SerializedName("createdAt")
    val createdAt: String
)