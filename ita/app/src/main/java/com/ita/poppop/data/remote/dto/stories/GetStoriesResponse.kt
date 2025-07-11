package com.ita.poppop.data.remote.dto.stories

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetStoriesResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<StoryData>?
)
@Parcelize
data class StoryData(
    @SerializedName("storyId")
    val storyId: Int,

    @SerializedName("popupId")
    val popupId: Int,

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
): Parcelable