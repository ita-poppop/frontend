package com.ita.poppop.data.remote.repository.popup

import com.ita.poppop.data.remote.dto.comments.DeleteCommentResponse
import com.ita.poppop.data.remote.dto.comments.GetCommentListResponse
import com.ita.poppop.data.remote.dto.comments.GetCommentResponse
import com.ita.poppop.data.remote.dto.comments.PostCommentRequest
import com.ita.poppop.data.remote.dto.comments.PostCommentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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

    @POST("/api/v1/popups/{popupId}/reviews/{reviewId}/comments")
    suspend fun postComment(
        @Header("Authorization") accessToken: String,
        @Path("reviewId") reviewId: Int,
        @Body request: PostCommentRequest
    ): Response<PostCommentResponse>

    @DELETE("/api/v1/popups/{popupId}/reviews/{reviewId}/comments/{commentId}/delete")
    suspend fun deleteComment(
        @Header("Authorization") accessToken: String,
        @Path("commentId") commentId: Int
    ): Response<DeleteCommentResponse>
}