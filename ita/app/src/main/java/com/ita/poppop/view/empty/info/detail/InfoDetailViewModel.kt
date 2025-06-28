package com.ita.poppop.view.empty.info.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.dto.PopupDetailData
import com.ita.poppop.data.remote.repository.popup.PopupDetailRepository
import kotlinx.coroutines.launch

class InfoDetailViewModel(
    private val repository: PopupDetailRepository
) : ViewModel() {

    private val _infoDetail = MutableLiveData<PopupDetailData>()
    val infoDetail: LiveData<PopupDetailData> = _infoDetail

    fun getInfoDetail(popupId: Int) {
        viewModelScope.launch {
            try {
                val data = repository.getPopupDetail(popupId)
                data?.let {
                    _infoDetail.value = it.copy(
                        title = "",
                        imageUrl = "",
                        location = "",
                        date = ""
                    )
                }
            } catch (e: Exception) {
                Log.e("InfoDetailApi", "Error: ${e.message}")
            }
        }
    }
}