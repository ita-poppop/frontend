package com.ita.poppop.view.empty.info.detail

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import com.ita.poppop.data.remote.repository.popups.PopupsRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class InfoDetailViewModel(
    private val accessToken: String,
    private val repository: PopupsRepository
) : ViewModel() {

    private val _infoDetail = MutableLiveData<Spanned>()
    val infoDetail: LiveData<Spanned> = _infoDetail

    fun getInfoDetail(popupId: Int) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getDetailPopups(accessToken, popupId)
                }
                if (result.isSuccessful) {
                    result.body()?.data?.let { data ->
                        val comment = data.comment ?: ""
                        val detail = data.detail ?: ""

                        val fullText = "$comment\n\n$detail"
                        val spannable = SpannableString(fullText)

                        // comment 부분에 스타일 적용
                        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, comment.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannable.setSpan(RelativeSizeSpan(1.2f), 0, comment.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                        _infoDetail.postValue(spannable)
                    }
                    /*result.body()?.data?.let { data ->
                        val combinedText = "${data.comment}\n\n${data.detail}"
                        _infoDetail.postValue(combinedText)
                    }*/
                    Log.d("InfoDetailApi_SUCCESS", "${result}")
                }
            } catch (e: HttpException) {
                // HTTP 에러 상세 정보
                Log.e("InfoDetailAPI_ERROR", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("InfoDetailAPI_ERROR", "Exception: ${e.message}", e)
            }
        }
    }
}