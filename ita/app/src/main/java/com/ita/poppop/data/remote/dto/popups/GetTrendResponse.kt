package com.ita.poppop.data.remote.dto.popups

import com.google.gson.annotations.SerializedName

data class GetTrendResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<TrendData>
)

data class TrendData(
    @SerializedName("title")
    val title: String,

    @SerializedName("imageUrl")
    val imageUrl: String,

    @SerializedName("location")
    val location: String
)

//data class GetPopupResponse(
//    val code: String,
//    val message: String,
//    val data: List<PopupItem>
//)
//
//data class PopupItem(
//    val title: String,
//    val imageUrl: String,
//    val location: String
//)
