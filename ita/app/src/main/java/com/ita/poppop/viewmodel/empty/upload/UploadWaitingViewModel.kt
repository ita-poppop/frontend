package com.ita.poppop.viewmodel.empty.upload

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.ita.poppop.data.remote.dto.popups.SearchData
import com.ita.poppop.view.empty.home_upload.sub.ImageItem
import com.ita.poppop.view.empty.home_upload.sub.UploadItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


class UploadWaitingViewModel: ViewModel() {


    private val _waitingImage = MutableLiveData<ImageItem?>()
    val waitingImage: LiveData<ImageItem?> get() = _waitingImage

    // set 함수
    fun setWaitingImage(item: ImageItem) {
        _waitingImage.value = item
    }

    // get 함수
    fun getWaitingImage(): ImageItem? {
        return _waitingImage.value
    }

    // remove 함수 (null로 초기화)
    fun removeWaitingImage() {
        _waitingImage.value = null
    }




    private val _popupItem = MutableLiveData<SearchData?>()
    val popupItem: LiveData<SearchData?> get() = _popupItem

    // set 함수
    fun setPopupItem(item: SearchData?) {
        _popupItem.value = item
    }

    fun createMultipartFromWaitingImage(context: Context): MultipartBody.Part? {
        val uri = waitingImage.value?.uri ?: return null

        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()

            val requestBody = tempFile
                .asRequestBody("image/jpeg".toMediaTypeOrNull())

            MultipartBody.Part.createFormData("photo", tempFile.name, requestBody) // ✅ 키 이름이 photo여야 함

        } catch (e: Exception) {
            Log.e("UploadViewModel", "Multipart 생성 실패: ${e.message}")
            null
        }
    }


    private val _waitingCount = MutableLiveData<Int?>()
    val waitingCount: LiveData<Int?> get() = _waitingCount

    // 양방향 바인딩을 위한 setter 함수
    fun setWaiting(count: Int?) {  
        _waitingCount.value = count
    }


    val isAllValid = MediatorLiveData<Boolean>().apply {
        val validator = {
//            value = waitingImage.value != null &&
//                    waitingCount.value  != null &&
//                    !popupItem.value.isNullOrBlank()
            value = waitingImage.value != null &&
                    popupItem.value != null &&
                    waitingCount.value != null
        }
        addSource(waitingImage) { validator() }
        addSource(popupItem) { validator() }
        addSource(waitingCount) { validator() }
    }
}