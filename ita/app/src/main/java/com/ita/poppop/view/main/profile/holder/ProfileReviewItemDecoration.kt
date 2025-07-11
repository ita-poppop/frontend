package com.ita.poppop.view.main.profile.holder

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
import com.ita.poppop.data.remote.dto.popups.PlannedData
import com.ita.poppop.data.remote.dto.profile.ReviewItem
import com.ita.poppop.view.empty.upcoming.holder.UpcomingItemDecoration
import com.ita.poppop.view.empty.upcoming.holder.UpcomingItemDecoration.Companion

class ProfileReviewItemDecoration(
    private val context: Context,
    private val itemList: List<ReviewItem>
) : RecyclerView.ItemDecoration() {
    companion object {
        private const val SPACING_UNIT = 3.0f
        private const val TOP_SPACING_DP = 10
        private const val SIDE_SPACING_DP = 10
        private const val COLUMN_COUNT = 2
    }
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val rightSpacing = (SIDE_SPACING_DP * SPACING_UNIT).toInt()
        outRect.right = rightSpacing  // 오른쪽에만 간격 추가
    }
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        if(itemList.size == 0){
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

            // dp를 px로 변환
            val textMarginBottom = (50 * density).toInt()  // 50dp를 px로 변환
            val iconMarginTop = (16 * density).toInt()      // 8dp를 px로 변환

            // 텍스트 Y 위치: 부모뷰 아래쪽으로부터 50dp 위
            val textY = parent.height - textMarginBottom.toFloat()

            // 아이콘 위치 계산 (가로 기준)
            val iconWidthDp = 100 // 원하는 아이콘 가로 크기(dp)
            val iconWidth = (iconWidthDp * density).toInt() // dp를 px로 변환

            // 가로 기준으로 세로 크기 계산 (비율 유지)
            val iconHeight = icon?.let {
                (iconWidth.toFloat() * it.intrinsicHeight / it.intrinsicWidth).toInt()
            } ?: iconWidth

            val iconBottom = textY - iconMarginTop  // 텍스트 위쪽으로부터 8dp 위
            val iconTop = iconBottom - iconHeight
            val iconLeft = centerX - iconWidth / 2
            val iconRight = centerX + iconWidth / 2

            // 이미지 그리기
            icon?.let {
                it.setBounds(iconLeft, iconTop.toInt(), iconRight, iconBottom.toInt())
                it.draw(c)
            }

            // 텍스트 그리기
            c.drawText(getString(context, R.string.empty_review), centerX.toFloat(), textY, paint)
        }else{
            parent.setBackgroundResource(R.color.transparent)
        }
    }
}
