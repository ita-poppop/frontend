package com.ita.poppop.data.remote.api

import com.google.gson.annotations.SerializedName
import com.ita.poppop.data.remote.dto.popups.GetPlannedResponse
import com.ita.poppop.data.remote.dto.popups.GetPopupDetailResponse
import com.ita.poppop.data.remote.dto.popups.GetSearchResponse
import com.ita.poppop.data.remote.dto.popups.GetTrendResponse
import com.ita.poppop.data.remote.dto.profile.GetProfileResponse
import com.ita.poppop.data.remote.dto.profile.PostProfileResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ProfileApi {
    @Multipart
    @POST("/api/v1/profile/update")
    suspend fun postProfile(
        @Header("Authorization") accessToken: String,
        @Part profileImage: MultipartBody.Part?,
        @Part("userName") userName: String
    ): Response<PostProfileResponse>


    @GET("/api/v1/profile")
    suspend fun getProfile(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetProfileResponse>

}

