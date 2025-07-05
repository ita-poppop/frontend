package com.ita.poppop.data.remote.api

import com.ita.poppop.data.remote.dto.GetLoginResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface LoginApi {
    @GET("/api/v1/popups/{popupId}")
    suspend fun getLogin(
        @Query("popupId") popupId: Int
    ): Response<GetLoginResponse>
}