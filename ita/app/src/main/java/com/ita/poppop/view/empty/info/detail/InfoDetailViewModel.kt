package com.ita.poppop.view.empty.info.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.repository.popup.TrendRepository
import kotlinx.coroutines.Dispatchers
import com.ita.poppop.data.remote.dto.popups.PopupDetailData
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class InfoDetailViewModel(
    private val repository: TrendRepository
) : ViewModel() {

    private val _infoDetail = MutableLiveData<PopupDetailData>()
    val infoDetail: LiveData<PopupDetailData> = _infoDetail

    fun getInfoDetail(popupId: Int) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getPopupDetail(1325)
                }
                if (result.isSuccessful) {
                    Log.d("InfoDetailApi_SUCCESS", "${result}")
                }
            } catch (e: HttpException) {
                // HTTP 에러 상세 정보
                Log.e("InfoDetailAPI_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("InfoDetailAPI_ERROR", "Exception: ${e.message}", e)
            }
        }
    }
}