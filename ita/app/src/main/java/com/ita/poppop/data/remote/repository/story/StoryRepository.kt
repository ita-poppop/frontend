package com.ita.poppop.data.remote.repository.story

import com.ita.poppop.data.remote.dto.popups.GetPlannedResponse
import com.ita.poppop.data.remote.dto.popups.GetPopupDetailResponse
import com.ita.poppop.data.remote.dto.popups.GetSearchResponse
import com.ita.poppop.data.remote.dto.popups.GetTrendResponse
import com.ita.poppop.data.remote.dto.story.GetStoryDetailResponse
import com.ita.poppop.data.remote.dto.story.GetStoryResponse
import com.ita.poppop.data.remote.dto.story.PostDeleteStoryResponse
import com.ita.poppop.data.remote.dto.story.PostUploadStoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StoryRepository {
    @GET("/api/v1/popups/{popupId}/stories")
    suspend fun getStory(
        @Query("popupId") popupId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetStoryResponse>

    @GET("/api/v1/popups/{popupId}/stories")
    suspend fun postUploadStory(
        @Query("popupId") popupId: Int
    ): Response<PostUploadStoryResponse>

    @GET("/api/v1/popups/{popupId}/stories/{storyId}/delete")
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
