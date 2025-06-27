package com.ita.poppop.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ita.poppop.view.empty.home_upload.sub.ImageItem

class MainViewModel: ViewModel() {


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