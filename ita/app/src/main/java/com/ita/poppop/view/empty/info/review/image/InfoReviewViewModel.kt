package com.ita.poppop.view.empty.info.review.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ita.poppop.R

class InfoReviewImageViewModel: ViewModel() { // 삭제 가능
    private val _inforeviewimageList = MutableLiveData<MutableList<InfoReviewImageRVItem>>()
    val inforeviewimageList: LiveData<MutableList<InfoReviewImageRVItem>> = _inforeviewimageList

    fun getInfoReviewImage(){
        val list = mutableListOf<InfoReviewImageRVItem>()
        /*list.clear()*/
        list.add(
            InfoReviewImageRVItem(
                1,
                "R.drawable.main_btn_favorites_icon"
            )
        )
        list.add(
            InfoReviewImageRVItem(
                2,
                "R.drawable.main_btn_map_icon"
            )
        )
        list.add(
            InfoReviewImageRVItem(
                3,
                "R.drawable.main_btn_favorites_icon"
            )
        )

        _inforeviewimageList.value = list
    }

}