package com.ita.poppop.data.remote.repository.popups

import com.ita.poppop.data.remote.api.PopupApi
import com.ita.poppop.data.remote.dto.popups.GetLocationPopupResponse
import com.ita.poppop.data.remote.dto.popups.GetPlannedResponse
import com.ita.poppop.data.remote.dto.popups.GetPopupDetailResponse
import com.ita.poppop.data.remote.dto.popups.GetSearchMapResponse
import com.ita.poppop.data.remote.dto.popups.GetSearchResponse
import com.ita.poppop.data.remote.dto.popups.GetTrendResponse
import retrofit2.HttpException
import retrofit2.Response


class PopupsRepositoryImpl(
    private val api: PopupApi
) : PopupsRepository {
    override suspend fun getDetailPopups(
        accessToken: String,
        popupId: Int
    ): Response<GetPopupDetailResponse> {
        try {
            val response = api.getPopupDetail("Bearer $accessToken",popupId)

            if (response.code() == 200) {
                return response
            } else {
                throw Exception("API 응답 오류: ${response.message()} (코드: ${response.code()})")
            }
        } catch (e: HttpException) {
            // HTTP 500 에러 등을 명확하게 전달
            throw Exception("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getTrendPopups(
        page: Int,
        size: Int
    ): Response<GetTrendResponse> {
        try {
            val response = api.getTrends(page, size)

            if (response.code() == 200) {
                return response
            } else {
                throw Exception("API 응답 오류: ${response.message()} (코드: ${response.code()})")
            }
        } catch (e: HttpException) {
            // HTTP 500 에러 등을 명확하게 전달
            throw Exception("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSearchPopups(
        requestDto: String,
        page: Int,
        size: Int
    ): Response<GetSearchResponse> {
        try {
            val response = api.getSearch(requestDto,page, size)

            if (response.code() == 200) {
                return response
            } else {
                throw Exception("API 응답 오류: ${response.message()} (코드: ${response.code()})")
            }
        } catch (e: HttpException) {
            // HTTP 500 에러 등을 명확하게 전달
            throw Exception("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSearchMap(
        content: String,
        page: Int,
        size: Int
    ): Response<GetSearchMapResponse> {
        try {
            val response = api.getSearchMap(content,page, size)

            if (response.code() == 200) {
                return response
            } else {
                throw Exception("API 응답 오류: ${response.message()} (코드: ${response.code()})")
            }
        } catch (e: HttpException) {
            // HTTP 500 에러 등을 명확하게 전달
            throw Exception("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getPlannedPopups(
        page: Int,
        size: Int
    ): Response<GetPlannedResponse> {
        try {
            val response = api.getPlanned(page, size)

            if (response.code() == 200) {
                return response
            } else {
                throw Exception("API 응답 오류: ${response.message()} (코드: ${response.code()})")
            }
        } catch (e: HttpException) {
            // HTTP 500 에러 등을 명확하게 전달
            throw Exception("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getLocationPopup(
        longitude: Double,
        latitude: Double,
        page: Int,
        size: Int
    ): Response<GetLocationPopupResponse> {
        try {
            val response = api.getLocationPopup(longitude, latitude, page, size)

            if (response.code() == 200) {
                return response
            } else {
                throw Exception("API 응답 오류: ${response.message()} (코드: ${response.code()})")
            }
        } catch (e: HttpException) {
            // HTTP 500 에러 등을 명확하게 전달
            throw Exception("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }
}
