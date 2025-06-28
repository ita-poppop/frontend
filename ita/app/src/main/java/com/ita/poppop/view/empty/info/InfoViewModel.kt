package com.ita.poppop.view.empty.info

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.dto.PopupDetailData
import com.ita.poppop.data.remote.repository.popup.PopupDetailRepository
import kotlinx.coroutines.launch

class InfoViewModel(
    private val repository: PopupDetailRepository
) : ViewModel() {

    private val _infoData = MutableLiveData<PopupDetailData>()
    val infoData: LiveData<PopupDetailData> = _infoData

    fun getInfo(popupId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.getPopupDetail(popupId)
                result?.let {
                    _infoData.value = it.copy(
                        comment = "",
                        detail = ""
                    )
                }
            } catch (e: Exception) {
                Log.e("InfoApi", "Error: ${e.message}")
            }
        }
    }
}