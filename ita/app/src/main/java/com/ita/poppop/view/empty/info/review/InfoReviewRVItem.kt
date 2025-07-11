package com.ita.poppop.view.empty.info.review

import android.os.Parcelable
import com.ita.poppop.view.empty.info.review.image.InfoReviewImageRVItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InfoReviewRVItem(
    var itemId: Int,
    var profileImage: String?,
    var username: String,
    var time: String,
    var reviewImage: List<InfoReviewImageRVItem>,
    var hearts: Int,
    var comments: Int,
    var content: String,
    val likedByUser: Boolean
) : Parcelable
