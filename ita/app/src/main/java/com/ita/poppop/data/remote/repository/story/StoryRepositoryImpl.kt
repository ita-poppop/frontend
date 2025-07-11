package com.ita.poppop.data.remote.repository.story

import com.ita.poppop.data.remote.api.StoryApi
import com.ita.poppop.data.remote.dto.story.GetStoryDetailResponse
import com.ita.poppop.data.remote.dto.story.GetStoryResponse
import com.ita.poppop.data.remote.dto.story.PostDeleteStoryResponse
import com.ita.poppop.data.remote.dto.story.PostUploadStoryResponse
import okhttp3.MultipartBody
import retrofit2.HttpException
import retrofit2.Response


class StoryRepositoryImpl(
    private val api: StoryApi
) : StoryRepository {

    override suspend fun getStory(
        accessToken: String,
        popupId: Int,
        page: Int,
        size: Int
    ): Response<GetStoryResponse> {
        try {
            val response = api.getStory("Bearer $accessToken", popupId, page, size)

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

    override suspend fun postUploadStory(
        accessToken: String,
        popupId: Int,
        images: MultipartBody.Part,
        estimatedWaitTime: Int,
        estimatedWaitCount: Int
    ): Response<PostUploadStoryResponse> {
        try {
            val response = api.postUploadStory("Bearer $accessToken",popupId,images,estimatedWaitTime,estimatedWaitCount)

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

    override suspend fun postDeleteStory(
        accessToken: String,
        popupId: Int,
        storyId: Int
    ): Response<PostDeleteStoryResponse> {
        try {
            val response = api.postDeleteStory("Bearer $accessToken",popupId,storyId)

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

    override suspend fun getStoryDetail(
        popupId: Int,
        storyId: Int
    ): Response<GetStoryDetailResponse> {
        try {
            val response = api.getStoryDetail(popupId,storyId)

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