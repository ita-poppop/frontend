package com.ita.poppop.data.remote.dto.bookmarks


data class GetBookmarkResponse(
    val code: String,
    val message: String,
    val data: List<BookmarkData>
)

data class BookmarkData(
    val popupId: Int,
    val title: String,
    val image: String,
    val location: String,
    val startDate: String,
    val endDate: String,
    val daysToStart: Int
)