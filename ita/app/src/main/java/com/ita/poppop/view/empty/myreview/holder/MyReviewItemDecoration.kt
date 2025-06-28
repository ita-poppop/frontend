package com.ita.poppop.view.empty.myreview.holder

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MyReviewItemDecoration : RecyclerView.ItemDecoration() {

    companion object {
        private const val SPACING_UNIT = 3.0f
        private const val TOP_SPACING_DP = 32
        private const val SIDE_SPACING_DP = 6
        private const val COLUMN_COUNT = 2
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val topSpacing = (TOP_SPACING_DP * SPACING_UNIT).toInt()
        outRect.bottom = topSpacing  // 오른쪽에만 간격 추가
    }
}
