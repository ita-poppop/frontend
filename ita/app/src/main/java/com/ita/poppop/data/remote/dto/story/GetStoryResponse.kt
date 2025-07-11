package com.ita.poppop.data.remote.dto.story

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class GetStoryResponse(
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
) : Parcelable