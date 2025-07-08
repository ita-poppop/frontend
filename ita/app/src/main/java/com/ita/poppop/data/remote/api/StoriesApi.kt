package com.ita.poppop.data.remote.api

import com.ita.poppop.data.remote.dto.stories.GetStoriesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StoriesApi {
    @GET("/api/v1/stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetStoriesResponse>
}