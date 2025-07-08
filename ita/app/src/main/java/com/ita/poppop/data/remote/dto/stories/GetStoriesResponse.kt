package com.ita.poppop.data.remote.dto.stories

import com.google.gson.annotations.SerializedName

data class GetStoriesResponse(
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

    @SerializedName("estimatedWaitTime")
    val estimatedWaitTime: Int,

    @SerializedName("estimatedWaitCount")
    val estimatedWaitCount: Int,

    @SerializedName("popupTitle")
    val popupTitle: String,

    @SerializedName("popupLocation")
    val popupLocation: String,

    @SerializedName("writerName")
    val writerName: String,

    @SerializedName("writerProfileUrl")
    val writerProfileUrl: String,

    @SerializedName("isRead")
    val isRead: Boolean,

    @SerializedName("createdAt")
    val createdAt: String
)