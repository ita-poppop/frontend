package com.ita.poppop.data.remote.dto.popups

import com.google.gson.annotations.SerializedName

data class GetPlannedResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<PlannedData>
)

data class PlannedData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("dday")
    val dday: String
)
