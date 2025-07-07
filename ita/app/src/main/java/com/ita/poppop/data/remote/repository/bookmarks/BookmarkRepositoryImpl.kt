package com.ita.poppop.data.remote.repository.popup

import com.ita.poppop.data.remote.api.BookmarkApi
import com.ita.poppop.data.remote.api.CommentApi
import com.ita.poppop.data.remote.dto.GetCommentListResponse
import com.ita.poppop.data.remote.dto.GetCommentResponse
import com.ita.poppop.data.remote.dto.bookmarks.DeleteBookmarkResponse
import com.ita.poppop.data.remote.dto.bookmarks.GetBookmarkResponse
import retrofit2.HttpException
import retrofit2.Response

class BookmarkRepositoryImpl(
    private val api: BookmarkApi
) : BookmarkRepository {

    override suspend fun getBookmarks(accessToken: String): Response<GetBookmarkResponse> {
        try {
            val response = api.getBookmarks("Bearer $accessToken")

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

    override suspend fun deleteBookmarks(
        accessToken: String,
        popupId: Int
    ): Response<DeleteBookmarkResponse> {
        try {
            val response = api.deleteBookmarks("Bearer $accessToken", popupId)

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