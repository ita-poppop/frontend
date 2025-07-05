package com.ita.poppop.data.remote.repository.Login

import com.ita.poppop.data.remote.dto.GetLoginResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query



interface LoginRepository {
    @GET("/api/v1/popups/{popupId}")
    suspend fun getLoginDetail(
        @Query("popupId") popupId: Int
    ): Response<GetLoginResponse>

}