package com.ita.poppop.view.empty.info.review.comment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InfoReviewCommentRVItem(
    var itemId: Int,
    var profileImage: String?,
    var username: String,
    var time: String,
    var content: String,
    var reply: Int?,
    val writerId: String? = null,
    var isMine: Boolean
) : Parcelable

