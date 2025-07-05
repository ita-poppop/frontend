package com.ita.poppop.data.remote.repository.popup

import com.ita.poppop.data.remote.dto.GetCommentListResponse
import com.ita.poppop.data.remote.dto.GetCommentResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentRepository {

    @GET("/api/v1/popups/{popupId}/reviews/{reviewId}/comments")
    suspend fun getCommentList(
        @Path("reviewId") reviewId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetCommentListResponse>

    @GET("/api/v1/popups/{popupId}/reviews/{reviewId}/comments/{commentId}")
    suspend fun getComment(
        @Path("reviewId") reviewId: Int,
        @Path("commentId") commentId: Int
    ): Response<GetCommentResponse>
}