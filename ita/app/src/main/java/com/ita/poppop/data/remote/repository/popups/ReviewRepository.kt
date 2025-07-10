package com.ita.poppop.data.remote.repository.popups

import com.ita.poppop.data.remote.dto.reviews.DeleteReviewResponse
import com.ita.poppop.data.remote.dto.reviews.GetReviewListResponse
import com.ita.poppop.data.remote.dto.reviews.GetReviewResponse
import com.ita.poppop.data.remote.dto.reviews.ModifyReviewResponse
import com.ita.poppop.data.remote.dto.reviews.PostReviewLikesResponse
import okhttp3.MultipartBody
import retrofit2.Response
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
        @Header("Authorization") accessToken: String,
        @Path("popupId") popupId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetReviewListResponse>

    @GET("/api/v1/popups/{popupId}/reviews/{reviewId}")
    suspend fun getReview(
        @Header("Authorization") accessToken: String,
        @Path("popupId") popupId: Int,
        @Path("reviewId") reviewId: Int,
    ): Response<GetReviewResponse>

    @POST("/api/v1/popups/{popupId}/reviews/{reviewId}/likes/toggle")
    suspend fun postReviewLikes(
        @Header("Authorization") accessToken: String,
        @Path("reviewId") reviewId: Int
    ): Response<PostReviewLikesResponse>

    @POST("/api/v1/popups/{popupId}/reviews/{reviewId}/delete")
    suspend fun deleteReview(
        @Header("Authorization") accessToken: String,
        @Path("reviewId") reviewId: Int
    ): Response<DeleteReviewResponse>

    @Multipart
    @POST("/api/v1/popups/{popupId}/reviews/{reviewId}/patch")
    suspend fun modifyReview(
        @Header("Authorization") accessToken: String,
        @Path("reviewId") reviewId: Int,
        @Part("content") content: String,
        @Part photo: List<MultipartBody.Part>,

    ): Response<ModifyReviewResponse>
}