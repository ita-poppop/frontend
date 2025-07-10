package com.ita.poppop.data.remote.repository.popups

import com.ita.poppop.data.remote.api.ReviewApi
import com.ita.poppop.data.remote.dto.reviews.DeleteReviewResponse
import com.ita.poppop.data.remote.dto.reviews.GetReviewListResponse
import com.ita.poppop.data.remote.dto.reviews.GetReviewResponse
import com.ita.poppop.data.remote.dto.reviews.ModifyReviewResponse
import com.ita.poppop.data.remote.dto.reviews.PostReviewLikesResponse
import okhttp3.MultipartBody
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
            val response = api.getReviewList("Bearer $accessToken", popupId, page, size)

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
            val response = api.getReview("Bearer $accessToken", popupId, reviewId)

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

    override suspend fun postReviewLikes(
        accessToken: String,
        reviewId: Int
    ): Response<PostReviewLikesResponse> {
        try {
            val response = api.postReviewLikes("Bearer $accessToken", reviewId)

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

    override suspend fun deleteReview(
        accessToken: String,
        reviewId: Int
    ): Response<DeleteReviewResponse> {
        try {
            val response = api.deleteReview("Bearer $accessToken", reviewId)

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

    override suspend fun modifyReview(
        accessToken: String,
        reviewId: Int,
        content: String,
        images: List<MultipartBody.Part>
    ): Response<ModifyReviewResponse> {
        try {
            val response = api.modifyReview("Bearer $accessToken", reviewId, content,images)

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