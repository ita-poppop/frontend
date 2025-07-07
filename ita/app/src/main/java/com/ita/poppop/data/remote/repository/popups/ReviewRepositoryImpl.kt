package com.ita.poppop.data.remote.repository.popup

import com.ita.poppop.data.remote.api.ReviewApi
import com.ita.poppop.data.remote.dto.reviews.GetReviewListResponse
import com.ita.poppop.data.remote.dto.reviews.GetReviewResponse
import retrofit2.HttpException
import retrofit2.Response

class ReviewRepositoryImpl(
    private val api: ReviewApi
) : ReviewRepository {
    override suspend fun getReviewList(
        popupId: Int,
        page: Int,
        size: Int
    ): Response<GetReviewListResponse> {
        try {
            val response = api.getReviewList(popupId, page, size)

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

    override suspend fun getReview(
        popupId: Int,
        reviewId: Int
    ): Response<GetReviewResponse> {
        try {
            val response = api.getReview(popupId, reviewId)

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