package com.ita.poppop.view.main.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ita.poppop.R
import com.ita.poppop.data.remote.dto.bookmarks.BookmarkData
import com.ita.poppop.data.remote.repository.popup.BookmarkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesViewModel(
    private val repository: BookmarkRepository
) : ViewModel() {

    private val _favoritesList = MutableLiveData<List<FavoritesRVItem>>()
    val favoritesList: LiveData<List<FavoritesRVItem>> = _favoritesList
    
    // 즐겨찾기 삭제
    fun deleteFavorites(popupId: Int) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHJpbmciLCJtZW1iZXJJZCI6MTAsInByb3ZpZGVySWQiOiJzdHJpbmciLCJuaWNrTmFtZSI6InN0cmluZyIsImVtYWlsIjoic3RyaW5nIiwicHJvZmlsZUltYWdlIjoic3RyaW5nIiwiaWF0IjoxNzUxOTYyOTg1LCJleHAiOjE3NTE5NjY1ODV9.4ETAugRypanJSSA7XKaSsI5xFTgslLtUyCk5lRxzPRA"
                    repository.deleteBookmarks(accessToken, popupId)
                }
                if (response.isSuccessful) {
                    response.body()?.let {
                        val currentList = _favoritesList.value ?: mutableListOf()
                        val updatedList = currentList.filterNot { it.itemId == popupId }.toMutableList()
                        _favoritesList.value = updatedList
                        Log.d("BookmarkApi_SUCCESS2", "deleteBookmark: $updatedList")
                    }
                } else {
                    Log.e("BookmarkApi_ERROR2", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("BookmarkApi_ERROR2", "Exception: ${e.message}", e)
            }
        }
       /* val currentFavoritesList = _favoritesList.value?.toMutableList() ?: return
        currentFavoritesList.removeAt(position)
        _favoritesList.value = currentFavoritesList.toList()*/
    }

    fun getFavorites(){
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHJpbmciLCJtZW1iZXJJZCI6MTAsInByb3ZpZGVySWQiOiJzdHJpbmciLCJuaWNrTmFtZSI6InN0cmluZyIsImVtYWlsIjoic3RyaW5nIiwicHJvZmlsZUltYWdlIjoic3RyaW5nIiwiaWF0IjoxNzUxOTYyOTg1LCJleHAiOjE3NTE5NjY1ODV9.4ETAugRypanJSSA7XKaSsI5xFTgslLtUyCk5lRxzPRA"
                    repository.getBookmarks(accessToken)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val bookmarkItems = body.data.map { bookmarkListDtoToAdapterItem(it) }.toMutableList()
                        _favoritesList.value = bookmarkItems
                        Log.d("BookmarkApi_SUCCESS", "BookmarkList: $bookmarkItems")
                    }
                } else {
                    Log.e("BookmarktApi_ERROR", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("BookmarkApi_ERROR", "Exception: ${e.message}", e)
            }
        }
    }

    // 데이터 변환
    private fun bookmarkListDtoToAdapterItem(data: BookmarkData): FavoritesRVItem {
        val formattedStartDate = data.startDate.replace("-", ".")
        val formattedEndDate = data.endDate.replace("-", ".")
        val popupPeriod = "$formattedStartDate - $formattedEndDate"

        val dday = if (data.daysToStart < 0) {
            "D+${-data.daysToStart}"
        } else if (data.daysToStart == 0) {
            "D-day"
        } else {
            "D-${data.daysToStart}"
        }

        return FavoritesRVItem(
            itemId = data.popupId,
            imageUrl = data.image,
            location = data.location,
            title = data.title,
            period = popupPeriod,
            dday = dday
        )
    }

}