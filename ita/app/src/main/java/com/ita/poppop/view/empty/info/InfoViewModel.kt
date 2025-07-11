package com.ita.poppop.view.empty.info

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import com.ita.poppop.data.remote.dto.popups.PopupDetailData
import com.ita.poppop.data.remote.repository.popup.BookmarkRepository
import com.ita.poppop.data.remote.repository.popups.PopupsRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class InfoViewModel(
    private val accessToken: String,
    private val repository: PopupsRepository,
    private val repository2: BookmarkRepository
) : ViewModel() {

    private val _infoData = MutableLiveData<PopupDetailData>()
    val infoData: LiveData<PopupDetailData> = _infoData

    fun getInfo(popupId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getDetailPopups(accessToken, popupId)
                }
                if (response.isSuccessful) {
                    response.body()?.data?.let { data ->
                        _infoData.value = data
                        Log.d("InfoApi_SUCCESS", "Info: $data")
                    }
                } else {
                    Log.e("InfoApi_ERROR", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("InfoApi_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    fun postBookmark(popupId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository2.postBookmarks(accessToken, popupId)
                }
                if (response.isSuccessful) {
                    Log.d("BookmarkApi_post_SUCCESS", "Bookmark registered.")

                } else {
                    Log.e("BookmarkApi_post_ERROR", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("BookmarkApi_post_ERROR", "Exception: ${e.message}", e)
            }
        }
    }


}