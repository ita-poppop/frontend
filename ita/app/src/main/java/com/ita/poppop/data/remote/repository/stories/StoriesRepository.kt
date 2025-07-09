package com.ita.poppop.data.remote.repository.stories

import com.ita.poppop.data.remote.dto.stories.GetStoriesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface StoriesRepository {
    @GET("/api/v1/stories")
    suspend fun getStories(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetStoriesResponse>

}