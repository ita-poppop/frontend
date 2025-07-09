package com.ita.poppop.data.remote.api

import com.google.gson.annotations.SerializedName
import com.ita.poppop.data.remote.dto.reviews.GetReviewListResponse
import com.ita.poppop.data.remote.dto.reviews.GetReviewResponse
import com.ita.poppop.data.remote.dto.reviews.PostReviewLikesResponse

import com.ita.poppop.data.remote.dto.review.PostReviewResponse
import com.ita.poppop.data.remote.dto.reviews.PostDeleteReviewResponse
import com.ita.poppop.data.remote.dto.reviews.PostEditReviewResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewApi {

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
        @Part photo: List<MultipartBody.Part>,

    ): Response<PostReviewResponse>

    @POST("/api/v1/popups/{popupId}/reviews/{reviewId}/patch")
    suspend fun postEditReview(
        @Header("Authorization") accessToken: String,
        @Path("reviewId") reviewId: Int,
        @Body editRequest: EditRequest

        ): Response<PostEditReviewResponse>

    @POST("/api/v1/popups/{popupId}/reviews/{reviewId}/delete")
    suspend fun postDeleteReview(
        @Header("Authorization") accessToken: String,
        @Path("reviewId") reviewId: Int,
        ): Response<PostDeleteReviewResponse>

    @GET("/api/v1/popups/{popupId}/reviews/{reviewId}")
    suspend fun getReview(
        @Path("popupId") popupId: Int,
        @Path("reviewId") reviewId: Int,
    ): Response<GetReviewResponse>

    @POST("/api/v1/popups/{popupId}/reviews/{reviewId}/likes/toggle")
    suspend fun postReviewLikes(
        @Header("Authorization") accessToken: String,
        @Path("reviewId") reviewId: Int
    ): Response<PostReviewLikesResponse>

}

// 요청 데이터 클래스
data class EditRequest(
    @SerializedName("content")
    val content: String
)

