package com.ita.poppop.data.remote.repository.popups

import com.ita.poppop.data.remote.api.PopupApi
import com.ita.poppop.data.remote.dto.popups.PopupDetailData
import retrofit2.HttpException

class PopupDetailRepositoryImpl(
    private val api: PopupApi
) : PopupDetailRepository {

    override suspend fun getPopupDetail(popupId: Int): PopupDetailData? {
        try {
            val response = api.getPopupDetail(popupId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == "200") {
                    return body.data  // PopupDetailData? 반환
                } else {
                    throw Exception("API 응답 오류: ${body?.message} (code=${body?.code})")
                }
            } else {
                throw Exception("HTTP 오류: ${response.code()} ${response.message()}")
            }
        } catch (e: HttpException) {
            throw Exception("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }
}