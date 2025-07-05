package com.ita.poppop.data.remote.repository.popup

import com.ita.poppop.data.remote.api.CommentApi
import com.ita.poppop.data.remote.dto.GetCommentListResponse
import com.ita.poppop.data.remote.dto.GetCommentResponse
import retrofit2.HttpException
import retrofit2.Response

class CommentRepositoryImpl(
    private val api: CommentApi
) : CommentRepository {
    override suspend fun getCommentList(
        reviewId: Int,
        page: Int,
        size: Int
    ): Response<GetCommentListResponse> {
        try {
            val response = api.getCommentList(reviewId, page, size)

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

    override suspend fun getComment(
        reviewId: Int,
        commentId: Int
    ): Response<GetCommentResponse> {
        try {
            val response = api.getComment(reviewId, commentId)

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