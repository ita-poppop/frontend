package com.ita.poppop.view.main.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ita.poppop.R

class FavoritesViewModel: ViewModel() {
    private val _favoritesList = MutableLiveData<List<FavoritesRVItem>>()
    val favoritesList: LiveData<List<FavoritesRVItem>> = _favoritesList
    
    // 즐겨찾기 삭제
    fun deleteFavorites(position: Int) {
        val currentFavoritesList = _favoritesList.value?.toMutableList() ?: return
        currentFavoritesList.removeAt(position)
        _favoritesList.value = currentFavoritesList.toList()
    }

    fun getFavorites(){
        val list = mutableListOf<FavoritesRVItem>()
        /*list.clear()*/
        /*list.add(
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

}