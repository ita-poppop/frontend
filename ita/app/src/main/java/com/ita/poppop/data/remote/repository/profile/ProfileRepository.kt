package com.ita.poppop.data.remote.repository.profile

import com.ita.poppop.data.remote.dto.profile.GetProfileResponse
import com.ita.poppop.data.remote.dto.profile.PostProfileResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ProfileRepository {
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
        @Path("page") page: Int,
        @Path("size") size: Int
    ): Response<GetProfileResponse>
}