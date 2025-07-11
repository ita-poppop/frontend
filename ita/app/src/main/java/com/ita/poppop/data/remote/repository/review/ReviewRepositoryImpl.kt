package com.ita.poppop.data.remote.repository.review

import com.ita.poppop.data.remote.api.EditRequest
import com.ita.poppop.data.remote.api.ReviewApi
import com.ita.poppop.data.remote.dto.review.PostReviewResponse
import com.ita.poppop.data.remote.dto.reviews.GetReviewListResponse
import com.ita.poppop.data.remote.dto.reviews.GetReviewResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.ita.poppop.data.remote.dto.reviews.PostDeleteReviewResponse
import com.ita.poppop.data.remote.dto.reviews.PostEditReviewResponse
import com.ita.poppop.data.remote.dto.reviews.PostReviewLikesResponse
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
        content: RequestBody,
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

    override suspend fun postEditReview(
        accessToken: String,
        reviewId: Int,
        editRequest: EditRequest
    ): Response<PostEditReviewResponse> {
        try {
            val response = api.postEditReview("Bearer $accessToken",reviewId,editRequest)

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

    override suspend fun postDeleteReview(
        accessToken: String,
        reviewId: Int
    ): Response<PostDeleteReviewResponse> {
        try {
            val response = api.postDeleteReview("Bearer $accessToken",reviewId)

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

    override suspend fun postReviewLikes(
        accessToken: String,
        reviewId: Int
    ): Response<PostReviewLikesResponse> {
        try {
            val response = api.postReviewLikes("Bearer $accessToken",reviewId)

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