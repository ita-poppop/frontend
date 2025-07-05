package com.ita.poppop.data.remote.repository.stories

import com.ita.poppop.data.remote.api.StoriesApi
import com.ita.poppop.data.remote.dto.stories.GetStoriesResponse
import retrofit2.HttpException
import retrofit2.Response

class StoriesRepositoryImpl(
    private val api: StoriesApi
) : StoriesRepository {

    override suspend fun getStories(
        page: Int, size: Int
    ): Response<GetStoriesResponse> {
        try {
            val response = api.getStories(page, size)

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
