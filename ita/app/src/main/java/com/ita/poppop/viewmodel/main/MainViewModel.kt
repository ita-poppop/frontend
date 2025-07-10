package com.ita.poppop.viewmodel.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ita.poppop.R
import com.ita.poppop.data.remote.dto.popups.SearchData
import com.ita.poppop.view.empty.home_upload.sub.ImageItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class MainViewModel: ViewModel() {
    private val _selectItem = MutableLiveData<SearchData?>()
    val selectItem: LiveData<SearchData?> get() = _selectItem

    // set 함수
    fun setSelectItem(item: SearchData) {
        _selectItem.value = item
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