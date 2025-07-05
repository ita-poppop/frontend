package com.ita.poppop.data.remote.api

import com.ita.poppop.data.remote.dto.story.GetStoryDetailResponse
import com.ita.poppop.data.remote.dto.story.PostDeleteStoryResponse
import com.ita.poppop.data.remote.dto.story.PostUploadStoryResponse
import com.ita.poppop.data.remote.dto.story.GetStoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface StoryApi {
    @GET("/api/v1/popups/{popupId}/stories")
    suspend fun getStory(
        @Query("popupId") popupId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetStoryResponse>

    @POST("/api/v1/popups/{popupId}/stories")
    suspend fun postUploadStory(
        @Query("popupId") popupId: Int
    ): Response<PostUploadStoryResponse>

    @POST("/api/v1/popups/{popupId}/stories/{storyId}/delete")
    suspend fun postDeleteStory(
        @Query("popupId") popupId: Int,
        @Query("storyId") storyId: Int
    ): Response<PostDeleteStoryResponse>

    @GET("/api/v1/popups/{popupId}/stories/{storyId}")
    suspend fun getStoryDetail(
        @Query("popupId") popupId: Int,
        @Query("storyId") storyId: Int
    ): Response<GetStoryDetailResponse>
}