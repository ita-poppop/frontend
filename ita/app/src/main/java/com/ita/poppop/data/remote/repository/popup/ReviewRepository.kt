package com.ita.poppop.data.remote.repository.popup

import com.ita.poppop.data.remote.dto.GetReviewListResponse
import com.ita.poppop.data.remote.dto.GetReviewResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewRepository {

    @GET("/api/v1/popups/{popupId}/reviews")
    suspend fun getReviewList(
        @Path("popupId") popupId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetReviewListResponse>

    @GET("/api/v1/popups/{popupId}/reviews/{reviewId}")
    suspend fun getReview(
        @Path("popupId") popupId: Int,
        @Path("reviewId") reviewId: Int,
    ): Response<GetReviewResponse>
}