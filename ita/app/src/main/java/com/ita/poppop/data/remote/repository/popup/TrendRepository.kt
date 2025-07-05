package com.ita.poppop.data.remote.repository.popup


import com.ita.poppop.data.remote.dto.popups.GetPlannedResponse
import com.ita.poppop.data.remote.dto.popups.GetPopupDetailResponse
import com.ita.poppop.data.remote.dto.popups.GetTrendResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface TrendRepository {
    @GET("/api/v1/popups/trend")
    suspend fun getTrendPopups(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetTrendResponse>

    @GET("/api/v1/popups/planned")
    suspend fun getPlannedPopups(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetPlannedResponse>

    @GET("/api/v1/popups/{popupId}")
    suspend fun getPopupDetail(
        @Path("popupId") popupId: Int
    ): Response<GetPopupDetailResponse>
}

//interface PopupRepository {
//    suspend fun getPopups(page: Int, size: Int): List<PopupItem>
//}