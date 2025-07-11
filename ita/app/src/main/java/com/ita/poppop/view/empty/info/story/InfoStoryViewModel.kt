package com.ita.poppop.view.empty.info.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.data.remote.dto.story.StoryData
import com.ita.poppop.data.remote.repository.story.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoStoryViewModel(
    private val accessToken: String,
    private val repository: StoryRepository
) : ViewModel() {

    private val _infostoryList = MutableLiveData<MutableList<InfoStoryRVItem>>()
    val infostoryList: LiveData<MutableList<InfoStoryRVItem>> = _infostoryList

    fun getInfoStory(popupId: Int){
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getStory(accessToken, popupId, 1,30)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val StoryListItems = body.data?.map { InfoStoryListDtoToAdapterItem(it) }?.toMutableList()
                        _infostoryList.value = StoryListItems
                        Log.d("StoryListApi_SUCCESS", "LocationPopupList: $StoryListItems")
                    }
                } else {
                    Log.e("StoryListApi_ERROR", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("StoryListApi_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    private fun InfoStoryListDtoToAdapterItem(data: StoryData): InfoStoryRVItem {

        return InfoStoryRVItem(
            itemId = data.storyId,
            imageUrl = data.photoUrl,
            name = data.writerName,
            isRead = data.isRead
        )
    }
}