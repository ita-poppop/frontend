package com.ita.poppop.data.remote.api

import com.google.gson.annotations.SerializedName
import com.ita.poppop.data.remote.dto.member.GetMemberExistsResponse
import com.ita.poppop.data.remote.dto.member.GetMemberInfoResponse
import com.ita.poppop.data.remote.dto.member.PostLogoutResponse
import com.ita.poppop.data.remote.dto.member.PostRefreshResponse
import com.ita.poppop.data.remote.dto.member.PostSignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface MemberApi {
    @POST("/api/v1/member/signup")
    suspend fun postSignup(
        @Body signupRequest: SignupRequest
    ): Response<PostSignupResponse>

    @POST("/api/v1/member/refresh")
    suspend fun postRefresh(
        @Header("RefreshToken") refreshToken: String
    ): Response<PostRefreshResponse>

    @POST("/api/v1/member/logout")
    suspend fun postLogout(
        @Header("Authorization") accessToken: String
    ): Response<PostLogoutResponse>

    @GET("/api/v1/member/me")
    suspend fun getMemberInfo(
        @Header("Authorization") accessToken: String
    ): Response<GetMemberInfoResponse>

    @GET("/api/v1/member/exists")
    suspend fun getMemberExists(
        @Query("email") email: String
    ): Response<GetMemberExistsResponse>
}

// 요청 데이터 클래스
data class SignupRequest(
    @SerializedName("providerId")
    val providerId: String,

    @SerializedName("registerId")
    val registerId: String, // "KAKAO", "GOOGLE" 등

    @SerializedName("nickName")
    val nickName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("profileImage")
    val profileImage: String
)
