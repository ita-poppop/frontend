package com.ita.poppop.viewmodel.main

import android.util.Log
import android.content.Context
import android.graphics.Bitmap

import android.graphics.Canvas

import androidx.appcompat.content.res.AppCompatResources
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ita.poppop.R
import com.ita.poppop.data.remote.dto.popups.SearchData
import com.ita.poppop.data.remote.dto.stories.StoryData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

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



    private val _userData = MutableLiveData<UserProfile?>()
    val userData: LiveData<UserProfile?> get() = _userData

    fun setUser(user: UserProfile){
        _userData.value = user
    }

    fun editUserProfile(userName: String, userProfileImage: String) {
        userData.value?.let { currentData ->
            val updatedData = currentData.copy(
                userName = userName,
                userProfileImage = userProfileImage
            )
            _userData.value = updatedData
        }
    }
    fun editUserProfileImage(userProfileImage: String) {
        userData.value?.let { currentData ->
            val updatedData = currentData.copy(
                userProfileImage = userProfileImage
            )
            _userData.value = updatedData
        }
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
    fun createMultipartFromProfileImage(context: Context): MultipartBody.Part? {
        val uriString = userData.value?.userProfileImage
        val uri = uriString?.toUri()

        return try {
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)

            if (uri != null) {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    FileOutputStream(tempFile).use { output ->
                        inputStream.copyTo(output)
                    }
                    inputStream.close()
                } else {
                    throw Exception("Failed to open InputStream from URI")
                }
            } else {
                // SVG를 Bitmap으로 변환
                val drawable = AppCompatResources.getDrawable(context, R.drawable._profile_load_icon)
                    ?: throw Exception("Failed to load drawable")

                val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 100
                val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 100
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)

                FileOutputStream(tempFile).use { out ->
                    val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    if (!success) throw Exception("Bitmap compress failed")
                }
            }

            val requestBody = tempFile
                .asRequestBody("image/jpeg".toMediaTypeOrNull())

            MultipartBody.Part.createFormData("profileImage", tempFile.name, requestBody)

        } catch (e: Exception) {
            Log.e("UploadViewModel", "Multipart 생성 실패: ${e.message}", e)
            null
        }
    }

}

data class UserProfile(
    val userName: String?,
    val userProfileImage: String?,
    val userLikesCount: Int?,
    val userReviewCount: Int?
)
