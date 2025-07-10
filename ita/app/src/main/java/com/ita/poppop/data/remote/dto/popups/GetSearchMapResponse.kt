package com.ita.poppop.data.remote.dto.popups

data class GetSearchMapResponse(
    val code: String,
    val message: String,
    val data: List<LocationData>
)
