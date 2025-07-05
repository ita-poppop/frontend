package com.ita.poppop.data.remote.dto.popups

data class GetPopupDetailResponse(
    val code: String,
    val message: String,
    val data: PopupDetailData?
)

data class PopupDetailData(
    val id:	Int,
    val title: String,
    val imageUrl: String,
    val location: String,
    val date: String,
    val comment: String,
    val detail:	String
)