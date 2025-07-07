package com.ita.poppop.data.remote.dto.bookmarks


data class DeleteBookmarkResponse(
    val code: String,
    val message: String,
    val data: BookmarkData?
)
