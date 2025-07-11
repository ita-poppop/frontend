package com.ita.poppop.view.main.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.dto.popups.LocationData
import com.ita.poppop.data.remote.repository.popups.PopupsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(
    private val repository: PopupsRepository
) : ViewModel() {

    private val _mapList = MutableLiveData<List<MapRVItem>>()
    val mapList: LiveData<List<MapRVItem>> = _mapList

    fun getLocationPopup(longitude: Double, latitude: Double){
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getLocationPopup(longitude, latitude, 1,30)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val locationPopupItems = body.data.map { locationPopupListDtoToAdapterItem(it) }.toMutableList()
                        _mapList.value = locationPopupItems
                        Log.d("LocationPopupApi_SUCCESS", "LocationPopupList: $locationPopupItems")
                    }
                } else {
                    Log.e("LocationPopupApi_ERROR", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("LocationPopupApi_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    private fun locationPopupListDtoToAdapterItem(data: LocationData): MapRVItem {

        val newDate = data.date
            .replace("-", ".")
            .replace("~", "-")

        return MapRVItem(
            itemId = data.id,
            lat = data.latitude,
            lng = data.longitude,
            imageUrl = data.imageUrl,
            title = data.title,
            period = newDate,
        )
    }
}