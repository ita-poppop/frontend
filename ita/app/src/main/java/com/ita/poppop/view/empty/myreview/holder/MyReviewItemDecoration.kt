package com.ita.poppop.view.empty.myreview.holder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.ita.poppop.R
import com.ita.poppop.data.remote.dto.profile.ReviewItem

class MyReviewItemDecoration(
    private val context: Context,
    private val itemList: List<ReviewItem>
) : RecyclerView.ItemDecoration() {

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
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        if (itemList.isEmpty()) {
            parent.setBackgroundColor(Color.TRANSPARENT) // 배경 색 지정

            val icon = ContextCompat.getDrawable(parent.context, R.drawable.icon_mono)
            val paint = Paint().apply {
                color = Color.DKGRAY
                textSize = 50f
                typeface = ResourcesCompat.getFont(parent.context, R.font.pretendard_semibold)
                textAlign = Paint.Align.CENTER
            }

            val centerX = parent.width / 2
            val density = parent.context.resources.displayMetrics.density

            // 아이콘 크기 및 위치 계산
            val iconWidthDp = 100
            val iconWidth = (iconWidthDp * density).toInt()
            val iconHeight = icon?.let {
                (iconWidth.toFloat() * it.intrinsicHeight / it.intrinsicWidth).toInt()
            } ?: iconWidth

            val iconTop = (parent.height / 2) - iconHeight / 2
            val iconLeft = centerX - iconWidth / 2
            val iconRight = centerX + iconWidth / 2
            val iconBottom = iconTop + iconHeight

            icon?.let {
                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                it.draw(c)
            }

            // 텍스트 위치 계산 (아이콘 아래 16dp)
            val textMarginTopDp = 16
            val textY = iconBottom + (textMarginTopDp * density)

            // 텍스트 그리기
            c.drawText(getString(context, R.string.empty_review), centerX.toFloat(), textY, paint)
        } else {
            parent.setBackgroundResource(R.color.transparent)
        }
    }

}
