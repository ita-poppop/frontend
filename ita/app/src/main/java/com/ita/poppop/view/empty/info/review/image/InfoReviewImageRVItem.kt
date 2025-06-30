package com.ita.poppop.view.empty.info.review.image

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InfoReviewImageRVItem(
    var itemId: Int,
    var imageUrl: String
) : Parcelable