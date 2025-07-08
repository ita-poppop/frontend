package com.ita.poppop.view.empty.info.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import com.ita.poppop.data.remote.repository.popups.PopupsRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class InfoDetailViewModel(
    private val repository: PopupsRepository
) : ViewModel() {

    private val _infoDetail = MutableLiveData<String>()
    val infoDetail: LiveData<String> = _infoDetail

    fun getInfoDetail(popupId: Int) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHJpbmciLCJtZW1iZXJJZCI6MTAsInByb3ZpZGVySWQiOiJzdHJpbmciLCJuaWNrTmFtZSI6InN0cmluZyIsImVtYWlsIjoic3RyaW5nIiwicHJvZmlsZUltYWdlIjoic3RyaW5nIiwiaWF0IjoxNzUxOTY5Nzk0LCJleHAiOjE3NTE5NzMzOTR9.MLJ-1Dw7DKDHtEXWnvgaiQYcqGNsaUJydWSH_WBP-og"
                    repository.getDetailPopups(accessToken, popupId)
                }
                if (result.isSuccessful) {
                    result.body()?.data?.let { data ->
                        val combinedText = "${data.comment}\n\n${data.detail}"
                        _infoDetail.postValue(combinedText)
                    }
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