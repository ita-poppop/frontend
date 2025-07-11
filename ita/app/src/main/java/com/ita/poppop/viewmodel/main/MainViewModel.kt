package com.ita.poppop.viewmodel.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ita.poppop.data.remote.dto.popups.SearchData
import com.ita.poppop.data.remote.dto.stories.StoryData
import com.ita.poppop.view.empty.home_upload.sub.ImageItem

class MainViewModel: ViewModel() {

    // 업로드시 사용하는 아이템 선택
    private val _selectItem = MutableLiveData<SearchData?>()
    val selectItem: LiveData<SearchData?> get() = _selectItem

    // set 함수
    fun setSelectItem(item: SearchData?) {
        _selectItem.value = item
    }
    // 아이템 정보확인시 사용하는 아이템 선택
    private val _selectItemDetail = MutableLiveData<SearchData?>()
    val selectItemDetail: LiveData<SearchData?> get() = _selectItemDetail

    // set 함수
    fun setSelectItemDetail(item: SearchData?) {
        _selectItemDetail.value = item
    }


    private val _profileImage = MutableLiveData<ImageItem?>()
    val profileImage: LiveData<ImageItem?> get() = _profileImage

    // set 함수
    fun setProfileImage(item: ImageItem) {
        _profileImage.value = item
    }

    // get 함수
    fun getProfileImage(): ImageItem? {
        return _profileImage.value
    }


    // 내부에서만 수정 가능한 MutableLiveData
    private val _storyList = MutableLiveData<List<StoryData>>(emptyList())

    // 외부에 노출할 때는 불변형으로
    val storyList: LiveData<List<StoryData>> = _storyList

    // 리스트 전체를 설정하는 함수
    fun setStoryList(newList: List<StoryData>) {
        _storyList.value = newList
    }

    fun getStoriesFromStoryIdInOrder(storyId: Int): List<StoryData> {
        val list = _storyList.value ?: return emptyList()

        val index = list.indexOfFirst { it.storyId == storyId }

        return if (index != -1) {
            list.subList(index, list.size)
        } else {
            emptyList()
        }
    }

    // 특정 항목의 isRead 값을 true로 업데이트하는 함수
    fun markStoryAsRead(storyId: Int) {
        _storyList.value = _storyList.value?.map {
            if (it.storyId == storyId) it.copy(isRead = true) else it
        }
    }

    // isRead 기준으로 정렬해서 업데이트하는 함수
    fun sortByIsRead() {
        _storyList.value = _storyList.value?.sortedBy { it.isRead }
    }

//    val isAllValid = MediatorLiveData<Boolean>().apply {
//        val validator = {
//            value = waitingImage.value != null &&
//                    waitingCount.value  != null &&
//                    !popupItem.value.isNullOrBlank()
//        }
//        addSource(waitingImage) { validator() }
//        addSource(popupItem) { validator() }
//        addSource(waitingCount) { validator() }
//    }
}