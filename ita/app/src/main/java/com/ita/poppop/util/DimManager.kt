package com.ita.poppop.util

import android.content.Context
import android.graphics.RectF
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.view.WindowInsetsControllerCompat

/**
 * 전체 화면 Dim 처리를 관리하는 클래스
 *
 * 화면에 어두운 오버레이를 표시하고, 특정 영역만 터치 가능하도록 관리합니다.
 * 연결된 뷰들의 가시성과 상태를 자동으로 관리합니다.
 */
class DimManager(private val window: Window) {

    companion object {
        private const val TAG = "DimManager"
        private const val ELEVATION_DP = 4f
    }

    // Core properties
    private var dimView: DimView? = null
    private var isDimActive = false

    // UI state management
    private val visibilityControlledViews = mutableListOf<View>()
    private var linkedToggleButton: ToggleButton? = null

    init {
        initializeDimView()
    }

    /**
     * Dim 화면을 표시합니다.
     */
    fun showDim() {
        if (isDimActive) return

        addDimViewToWindow()
        updateSystemBars(isDark = true)
        elevateControlledViews()
        updateToggleButton(isChecked = true)
        addFabTouchAreas()
        addTextTouchAreas()

        isDimActive = true
    }

    /**
     * Dim 화면을 숨깁니다.
     */
    fun hideDim() {
        if (!isDimActive) return

        removeDimViewFromWindow()
        updateSystemBars(isDark = false)
        lowerControlledViews()
        updateToggleButton(isChecked = false)
        clearAllTouchAreas()

        isDimActive = false
    }

    /**
     * 현재 Dim 상태를 반환합니다.
     */
    fun isDimmed(): Boolean = isDimActive

    /**
     * ToggleButton을 DimManager와 연결합니다.
     */
    fun bindToggleButton(toggleButton: ToggleButton) {
        linkedToggleButton = toggleButton
    }

    /**
     * 여러 FAB 버튼을 가시성 관리 대상에 추가합니다.
     */
    fun addFabButtons(fabButtons: List<View>) {
        visibilityControlledViews.addAll(fabButtons)
    }

    /**
     * 여러 FAB 버튼을 가시성 관리 대상에 추가합니다.
     */
    fun addFabText(fabTexts: List<View>) {
        visibilityControlledViews.addAll(fabTexts)
    }

    /**
     * 특정 뷰를 터치 가능 영역으로 추가합니다.
     */
    fun addTouchableView(view: View, shouldManageVisibility: Boolean = true) {
        val touchArea = createTouchAreaFromView(view)
        dimView?.addTouchableArea(touchArea)

        if (shouldManageVisibility) {
            addToVisibilityControl(view)
        }
    }

    /**
     * 원형 터치 가능 영역을 추가합니다.
     */
    fun addCircularTouchArea(centerX: Float, centerY: Float, radius: Float) {
        logTouchAreaAddition(centerX, centerY, radius)
        dimView?.addCircleTouchableArea(centerX, centerY, radius)
    }

    /**
     * 둥근 사각형 터치 가능 영역을 추가합니다.
     */
    fun addRoundRectTouchArea(rectF: RectF, cornerRadius: Float) {
        dimView?.addRoundRectTouchableArea(rectF, cornerRadius)
    }

    /**
     * 특정 뷰를 가시성 관리에서 제거합니다.
     */
    fun removeFromVisibilityControl(view: View) {
        visibilityControlledViews.remove(view)
    }

    /**
     * 리소스를 정리합니다.
     */
    fun release() {
        hideDim()
        visibilityControlledViews.clear()
        dimView = null
        linkedToggleButton = null
    }

    // ========================================
    // Private Implementation
    // ========================================

    private fun initializeDimView() {
        dimView = DimView(window.context).apply {
            onDimAreaClicked = { hideDim() }
        }
    }

    private fun addDimViewToWindow() {
        dimView?.let { view ->
            getDecorView().addView(view)
        }
    }

    private fun removeDimViewFromWindow() {
        dimView?.let { view ->
            getDecorView().removeView(view)
        }
    }

    private fun getDecorView(): ViewGroup = window.decorView as ViewGroup

    private fun updateSystemBars(isDark: Boolean) {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }
    }

    private fun elevateControlledViews() {
        val elevation = dpToPx(ELEVATION_DP, window.context)
        visibilityControlledViews.forEach { view ->
            view.elevation = elevation
        }
    }

    private fun lowerControlledViews() {
        val elevation = -dpToPx(ELEVATION_DP, window.context)
        visibilityControlledViews.forEach { view ->
            view.elevation = elevation
        }
    }

    private fun updateToggleButton(isChecked: Boolean) {
        linkedToggleButton?.isChecked = isChecked
    }

    private fun addFabTouchAreas() {
        // FAB 버튼들의 터치 영역 추가
        visibilityControlledViews.take(2).forEach { fab ->
            val (x, y, radius) = calculateViewPosition(fab)
            logViewPosition(x, y, radius, "FAB")
            addCircularTouchArea(x, y, radius)
        }

        // 연결된 토글 버튼의 터치 영역 추가
        linkedToggleButton?.let { toggle ->
            val (x, y, radius) = calculateTogglePosition(toggle)
            addCircularTouchArea(x, y, radius)
        }
    }
    private fun addTextTouchAreas() {
        // FAB 버튼들의 터치 영역 추가
        visibilityControlledViews.takeLast(2).forEach { text ->
            val (rectF, radius) = calculateRoundRectArea(text)
            addRoundRectTouchArea(rectF, radius)
        }
    }

    private fun clearAllTouchAreas() {
        dimView?.clearTouchableAreas()
    }

    private fun createTouchAreaFromView(view: View): RectF {
        val location = IntArray(2)
        view.getLocationInWindow(location)

        return RectF(
            location[0].toFloat(),
            location[1].toFloat(),
            (location[0] + view.width).toFloat(),
            (location[1] + view.height).toFloat()
        )
    }

    private fun addToVisibilityControl(view: View) {
        if (!visibilityControlledViews.contains(view)) {
            visibilityControlledViews.add(view)

            // Dim 상태가 아니면 뷰를 숨김
            if (!isDimActive) {
                view.visibility = View.GONE
            }
        }
    }

    private fun calculateViewPosition(view: View): Triple<Float, Float, Float> {
        val location = IntArray(2)
        view.getLocationInWindow(location)

        val centerX = location[0] + view.width / 2f
        val centerY = location[1] + view.height / 2f
        val radius = minOf(view.width, view.height) / 2f

        return Triple(centerX, centerY, radius)
    }

    private fun calculateTogglePosition(toggleButton: ToggleButton): Triple<Float, Float, Float> {
        val location = IntArray(2)
        toggleButton.getLocationInWindow(location)

        val centerX = location[0] + toggleButton.width / 2f
        val centerY = location[1] + toggleButton.height / 2f
        val radius = minOf(toggleButton.width, toggleButton.height) / 2f

        return Triple(centerX, centerY, radius)
    }

    private fun calculateRoundRectArea(textView: View): Pair<RectF, Float> {
        val location = IntArray(2)
        textView.getLocationInWindow(location)

        val left = location[0].toFloat()
        val top = location[1].toFloat()
        val right = left + textView.width
        val bottom = top + textView.height

        val rectF = RectF(left, top, right, bottom)
        val cornerRadius = 10 * 3.0f

        return Pair(rectF, cornerRadius)
    }


    private fun dpToPx(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

    private fun logTouchAreaAddition(centerX: Float, centerY: Float, radius: Float) {
        Log.d(TAG, "Adding circular touch area - centerX: $centerX, centerY: $centerY, radius: $radius")
    }

    private fun logViewPosition(x: Float, y: Float, radius: Float, viewType: String) {
        Log.d(TAG, "$viewType position - X: $x, Y: $y, radius: $radius")
    }
}