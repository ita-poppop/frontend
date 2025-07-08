package com.ita.poppop.util.preference

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.ita.poppop.R
import com.ita.poppop.databinding.PreferenceWithEndContentBinding
import com.ita.poppop.databinding.PreferenceWithEndSwitchBinding

class EndSwitchPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.preference.R.attr.preferenceStyle
) : Preference(context, attrs, defStyleAttr) {

    private var isChecked: Boolean = false
    private var switchView: SwitchCompat? = null

    init {
        layoutResource = R.layout.preference_with_end_switch
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        // ViewBinding을 수동으로 바인딩 (Preference는 자동 바인딩 불가)
        val itemView = holder.itemView
        val binding = PreferenceWithEndSwitchBinding.bind(itemView)

        binding.tvTitle.text = title

        // 스위치 참조 저장
        switchView = binding.sw

        // SharedPreferences에서 현재 값 가져오기
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        isChecked = sharedPrefs.getBoolean(key, getDefaultValue())

        // 스위치 상태 설정
        binding.sw.isChecked = isChecked

        // 스위치 클릭 리스너 설정
        binding.sw.setOnCheckedChangeListener { _, checked ->
            if (checked != isChecked) {
                isChecked = checked
                // SharedPreferences에 값 저장
                persistBoolean(checked)
                // 변경 리스너 호출
                callChangeListener(checked)
            }
        }

        // 전체 아이템 클릭 시에도 스위치 토글
        itemView.setOnClickListener {
            if (isEnabled) {
                val newValue = !isChecked
                binding.sw.isChecked = newValue
            }
        }
    }

    // 외부에서 스위치 상태를 설정할 수 있는 메서드
    fun setSwitchChecked(checked: Boolean) {
        if (isChecked != checked) {
            isChecked = checked
            switchView?.isChecked = checked
            persistBoolean(checked)
        }
    }

    // 현재 스위치 상태를 가져오는 메서드
    fun getSwitchChecked(): Boolean {
        return isChecked
    }

    // 기본값 설정
    private fun getDefaultValue(): Boolean {
        return when (key) {
            "set_1", "set_2", "set_3" -> true
            else -> false
        }
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        val defaultVal = defaultValue as? Boolean ?: getDefaultValue()
        isChecked = getPersistedBoolean(defaultVal)
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getBoolean(index, getDefaultValue())
    }
}