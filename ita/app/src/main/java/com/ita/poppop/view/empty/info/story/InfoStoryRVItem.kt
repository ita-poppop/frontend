package com.ita.poppop.view.empty.info.story

data class InfoStoryRVItem(
    val itemId: Int,
    val imageUrl: String,
    val name: String,
    val profileUrl: String,
    val isRead: Boolean,
    val createdAt: String,
    val popupId: Int
)