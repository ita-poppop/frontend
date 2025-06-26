package com.ita.poppop.data.remote.repository.popup

import com.ita.poppop.data.remote.api.PopupApi
import com.ita.poppop.data.remote.dto.GetPlannedResponse
import com.ita.poppop.data.remote.dto.GetTrendResponse
import com.ita.poppop.util.remote.RetrofitClient
import okhttp3.Response
import retrofit2.HttpException


//class TrendRepositoryImpl : TrendRepository {
//    private val service = RetrofitClient.getRetrofit()!!.create(TrendApi::class.java)
//
//    override suspend fun getTrendPopups(
//        token: String,
//        page: Int,
//        size: Int
//    ): Response<GetTrendResponse> {
//        val response = service.getTrends("Bearer $token", page, size)
//
//        return response
//    }
//}

//class PopupRepositoryImpl(
////    private val api: PopupApi
//
//) : PopupRepository {
//
//    private val service = RetrofitClient.getRetrofit()!!.create(PopupApi::class.java)
//
//    override suspend fun getPopups(page: Int, size: Int): List<PopupItem> {
//        val response = service.getTrends(page, size)
//
//        // 응답 코드가 문자열 "200" 또는 숫자인지 확인
//        if (response.code == "200" || response.code == "OK") {
//            return response.data
//        } else {
//            throw Exception("API 오류: ${response.message} (코드: ${response.code})")
//        }
//    }
//}

class TrendRepositoryImpl(
    private val api: PopupApi
) : TrendRepository {
    override suspend fun getTrendPopups(
        page: Int,
        size: Int
    ): retrofit2.Response<GetTrendResponse> {
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

    override suspend fun getPlannedPopups(
        page: Int,
        size: Int
    ): retrofit2.Response<GetPlannedResponse> {
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
}
//class PopupRepositoryImpl(
//    private val api: PopupApi
//) : PopupRepository {
//
//    override suspend fun getTrendPopups(page: Int, size: Int): List<PopupItem> {
//        try {
//            val response = api.getTrends(page, size)
//
//            if (response.code == "200") {
//                return response.data
//            } else {
//                throw Exception("API 응답 오류: ${response.message} (코드: ${response.code})")
//            }
//        } catch (e: HttpException) {
//            // HTTP 500 에러 등을 명확하게 전달
//            throw Exception("HTTP ${e.code()}: ${e.message()}")
//        } catch (e: Exception) {
//            throw e
//        }
//    }
//}