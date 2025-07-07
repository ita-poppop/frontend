package com.ita.poppop.data.remote.repository.Member

import com.ita.poppop.data.remote.api.SignupRequest
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



interface MemberRepository {
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