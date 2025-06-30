package com.ita.poppop.data.remote.repository.popup

import com.ita.poppop.data.remote.dto.GetPopupDetailResponse
import com.ita.poppop.data.remote.dto.GetReviewListResponse
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
}