package com.ita.poppop.data.remote.dto.popups

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetSearchResponse(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<SearchData>
)

@Parcelize
data class SearchData(
    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("id")
    val id: Int,

    @SerializedName("imageUrl")
    val imageUrl: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("date")
    val date: String?,

    @SerializedName("location")
    val location: String
) : Parcelable
