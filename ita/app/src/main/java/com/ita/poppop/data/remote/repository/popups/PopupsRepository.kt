package com.ita.poppop.data.remote.repository.popups

import com.ita.poppop.data.remote.dto.popups.GetLocationPopupResponse
import com.ita.poppop.data.remote.dto.popups.GetPlannedResponse
import com.ita.poppop.data.remote.dto.popups.GetPopupDetailResponse
import com.ita.poppop.data.remote.dto.popups.GetSearchMapResponse
import com.ita.poppop.data.remote.dto.popups.GetSearchResponse
import com.ita.poppop.data.remote.dto.popups.GetTrendResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface PopupsRepository {
    @GET("/api/v1/popups/{popupId}")
    suspend fun getDetailPopups(
        @Header("Authorization") accessToken: String,
        @Query("popupId") popupId: Int
    ): Response<GetPopupDetailResponse>

    @GET("/api/v1/popups/trend")
    suspend fun getTrendPopups(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetTrendResponse>

    @GET("/api/v1/popups/search")
    suspend fun getSearchPopups(
        @Query("title") title: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetSearchResponse>

    @GET("/api/v1/popups/search")
    suspend fun getSearchMap(
        @Query("content") content: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetSearchMapResponse>

    @GET("/api/v1/popups/planned")
    suspend fun getPlannedPopups(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetPlannedResponse>

    @GET("/api/v1/popups/location")
    suspend fun getLocationPopup(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetLocationPopupResponse>
}