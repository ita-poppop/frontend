package com.ita.poppop.data.remote.repository.review

import com.ita.poppop.data.remote.api.ReviewApi
import com.ita.poppop.data.remote.dto.review.PostReviewResponse
import com.ita.poppop.data.remote.dto.reviews.GetReviewListResponse
import com.ita.poppop.data.remote.dto.reviews.GetReviewResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Response


class ReviewRepositoryImpl(
    private val api: ReviewApi
) : ReviewRepository {
    override suspend fun getReviewList(
        accessToken: String,
        popupId: Int,
        page: Int,
        size: Int
    ): Response<GetReviewListResponse> {
        try {
            val response = api.getReviewList("Bearer $accessToken",popupId,page,size)

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

    override suspend fun postReview(
        accessToken: String,
        popupId: Int,
        content: String,
        images: List<MultipartBody.Part>
    ): Response<PostReviewResponse> {
        try {
            val response = api.postReview("Bearer $accessToken",popupId,content,images)

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
        accessToken: String,
        popupId: Int,
        reviewId: Int
    ): Response<GetReviewResponse> {

        try {
            val response = api.getReview("Bearer $accessToken",popupId,reviewId)

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