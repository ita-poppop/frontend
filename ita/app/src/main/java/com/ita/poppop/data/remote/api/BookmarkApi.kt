package com.ita.poppop.data.remote.api

import com.ita.poppop.data.remote.dto.bookmarks.DeleteBookmarkResponse
import com.ita.poppop.data.remote.dto.bookmarks.GetBookmarkResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface BookmarkApi {

    @GET("/api/v1/bookmarks")
    suspend fun getBookmarks(
        @Header("Authorization") accessToken: String
    ): Response<GetBookmarkResponse>

    @POST("/api/v1/popups/{popupId}/bookmark/delete")
    suspend fun deleteBookmarks(
        @Header("Authorization") accessToken: String,
        @Path("popupId") popupId: Int
    ): Response<DeleteBookmarkResponse>
}