package com.ita.poppop.data.remote.api

import com.ita.poppop.data.remote.dto.GetPlannedResponse
import com.ita.poppop.data.remote.dto.GetReviewListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewApi {

    @GET("/api/v1/popups/{popupId}/reviews")
    suspend fun getReviewList(
        @Path("popupId") popupId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetReviewListResponse>
}