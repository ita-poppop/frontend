package com.ita.poppop.data.remote.repository.review

import com.ita.poppop.data.remote.dto.GetReviewListResponse
import com.ita.poppop.data.remote.dto.GetReviewResponse
import com.ita.poppop.data.remote.dto.review.PostReviewResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewRepository {
    @GET("/api/v1/popups/{popupId}/reviews")
    suspend fun getReviewList(
        @Path("popupId") popupId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetReviewListResponse>

    @Multipart
    @POST("/api/v1/popups/{popupId}/reviews")
    suspend fun postReview(
        @Header("Authorization") accessToken: String,
        @Path("popupId") popupId: Int,
        @Part("content") content: String,
        @Part images: List<MultipartBody.Part>
    ): Response<PostReviewResponse>

    @GET("/api/v1/popups/{popupId}/reviews/{reviewId}")
    suspend fun getReview(
        @Path("popupId") popupId: Int,
        @Path("reviewId") reviewId: Int,
    ): Response<GetReviewResponse>
}