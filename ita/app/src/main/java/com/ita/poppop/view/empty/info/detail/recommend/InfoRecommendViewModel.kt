package com.ita.poppop.view.empty.info.detail.recommend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.dto.popups.TrendData
import com.ita.poppop.data.remote.repository.popups.PopupsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoRecommendViewModel(
    private val repository: PopupsRepository
) : ViewModel() {

    private val _infoTrendList = MutableLiveData<List<InfoRecommendRVItem>>()
    val infoTrendList: LiveData<List<InfoRecommendRVItem>> = _infoTrendList

    fun getInfoTrends() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getTrendPopups(1,5)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val infotrendItems = body.data.map { infotrendDtoToAdapterItem(it) }.toMutableList()
                        _infoTrendList.value = infotrendItems
                        Log.d("InfoTrendsApi_SUCCESS", "Info: $infotrendItems")
                    }
                    //return@launch
                } else {
                    Log.e("InfoTrendsApi_ERROR", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("InfoTrendsApi_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    // 데이터 변환
    private fun infotrendDtoToAdapterItem(data: TrendData): InfoRecommendRVItem {

        return InfoRecommendRVItem(
            itemId = data.id,
            title = data.title,
            imageUrl = data.imageUrl,
            location = data.location
        )
    }
}
