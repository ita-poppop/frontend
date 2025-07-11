package com.ita.poppop.data.remote.dto.popups

data class GetLocationPopupResponse(
    val code: String,
    val message: String,
    val data: List<LocationData>
)

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val id: Int,
    val imageUrl: String,
    val title: String,
    val date: String
)