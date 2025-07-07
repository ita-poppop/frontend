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
                    val accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsnoTspIDtmJUiLCJpZCI6IjQzMjQ0NzEzMTQiLCJuaWNrTmFtZSI6IuyehOykgO2YlSIsImVtYWlsIjoibGltanVuaHllbmdAZ21haWwuY29tIiwiUHJvZmlsZUltYWdlIjoiaHR0cHM6Ly9pbWcxLmtha2FvY2RuLm5ldC90aHVtYi9SNjQweDY0MC5xNzAvP2ZuYW1lPWh0dHBzOi8vdDEua2FrYW9jZG4ubmV0L2FjY291bnRfaW1hZ2VzL2RlZmF1bHRfcHJvZmlsZS5qcGVnIiwiaWF0IjoxNzUxODY3NDYyfQ.84sTRKySacPuKTTgZZXM0SUmxPD_8ujFobzpDx0HUSU"
                    repository.deleteBookmarks(accessToken, popupId)
                }
                if (response.isSuccessful) {
                    response.body()?.let {
                        val currentList = _favoritesList.value ?: mutableListOf()
                        val updatedList = currentList.filterNot { it.itemId == popupId }.toMutableList()
                        _favoritesList.value = updatedList
                        Log.d("BookmarkApi_SUCCESS2", "deletecomment: $updatedList")
                    }
                } else {
                    Log.e("BookmarkApi_ERROR2", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("BookmarkApi_ERROR2", "Exception: ${e.message}", e)
            }
        }
        /*val currentFavoritesList = _favoritesList.value?.toMutableList() ?: return
        currentFavoritesList.removeAt(position)
        _favoritesList.value = currentFavoritesList.toList()*/
    }

    fun getFavorites(){
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsnoTspIDtmJUiLCJpZCI6IjQzMjQ0NzEzMTQiLCJuaWNrTmFtZSI6IuyehOykgO2YlSIsImVtYWlsIjoibGltanVuaHllbmdAZ21haWwuY29tIiwiUHJvZmlsZUltYWdlIjoiaHR0cHM6Ly9pbWcxLmtha2FvY2RuLm5ldC90aHVtYi9SNjQweDY0MC5xNzAvP2ZuYW1lPWh0dHBzOi8vdDEua2FrYW9jZG4ubmV0L2FjY291bnRfaW1hZ2VzL2RlZmF1bHRfcHJvZmlsZS5qcGVnIiwiaWF0IjoxNzUxODY3NDYyfQ.84sTRKySacPuKTTgZZXM0SUmxPD_8ujFobzpDx0HUSU"
                    repository.getBookmarks(accessToken)
                }
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val bookmarkItems = body.data.map { bookmarkListDtoToAdapterItem(it) }.toMutableList()
                        _favoritesList.value = bookmarkItems
                        Log.d("BookmarkApi_SUCCESS", "ReviewCommentList: $bookmarkItems")
                    }
                } else {
                    Log.e("BookmarktApi_ERROR", "API error: ${response.message()} (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("BookmarkApi_ERROR", "Exception: ${e.message}", e)
            }
        }

        /*val list = mutableListOf<FavoritesRVItem>()
        *//*list.clear()*//*
        list.add(
            FavoritesRVItem(
                1,
                R.drawable.map_dummy_img,
                "수원시 서둔동",
                "K현대미술관 X 우주먼지 팝업스토어",
                "25.02.28 - 25.03.30",
                "D-3"
            )
        )
        list.add(
            FavoritesRVItem(
                2,
                R.drawable.map_dummy_img,
                "경기 김포시 양촌읍",
                "곽철이 X 더 닐라이 팝업스토어 곽철이 X 더 닐라이 팝업스토어",
                "25.03.31 - 25.03.31",
                "D-12"
            )
        )
        list.add(
            FavoritesRVItem(
                3,
                R.drawable.main_btn_favorites_icon,
                "서울 성동구",
                "르세라핌 2025 S/S 팝업스토어",
                "25.03.17 - 25.03.30",
                "D-9"
            )
        )
        list.add(
            FavoritesRVItem(
                4,
                R.drawable.map_dummy_img,
                "수원시 서둔동",
                "K현대미술관 X 우주먼지 팝업스토어",
                "25.02.28 - 25.03.30",
                "D-3"
            )
        )
        _favoritesList.value = list*/
    }

    // 데이터 변환
    private fun bookmarkListDtoToAdapterItem(data: BookmarkData): FavoritesRVItem {
        val image = "R.drawable.map_dummy_img"

        val popupPeriod = "${data.startDate} - ${data.endDate}"

        return FavoritesRVItem(
            itemId = data.popupId,
            imageUrl = image,
            location = data.location,
            title = data.title,
            period = popupPeriod,
            dday = data.daysToStart
        )
    }

}