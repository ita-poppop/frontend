package com.ita.poppop.data.remote.dto.popups

import com.google.gson.annotations.SerializedName

data class GetSearchResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<SearchData>
)

data class SearchData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("image")
    val image: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("location")
    val location: String
)