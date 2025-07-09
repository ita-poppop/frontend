package com.ita.poppop.data.remote.repository.popup

import com.ita.poppop.data.remote.dto.bookmarks.DeleteBookmarkResponse
import com.ita.poppop.data.remote.dto.bookmarks.GetBookmarkResponse
import com.ita.poppop.data.remote.dto.bookmarks.PostBookmarkResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BookmarkRepository {

    @GET("/api/v1/bookmarks")
    suspend fun getBookmarks(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetBookmarkResponse>

    @POST("/api/v1/popups/{popupId}/bookmark/delete")
    suspend fun deleteBookmarks(
        @Header("Authorization") accessToken: String,
        @Path("popupId") popupId: Int
    ): Response<DeleteBookmarkResponse>

    @POST("/api/v1/popups/{popupId}/bookmark")
    suspend fun postBookmarks(
        @Header("Authorization") accessToken: String,
        @Path("popupId") popupId: Int
    ): Response<PostBookmarkResponse>
}