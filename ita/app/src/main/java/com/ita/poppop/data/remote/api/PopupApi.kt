package com.ita.poppop.data.remote.api

import com.ita.poppop.data.remote.dto.GetPopupDetailResponse
import com.ita.poppop.data.remote.dto.GetPopupResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query


//interface AlarmApi {
//    @GET("/api/alarms/activity")
//    suspend fun getActivityAlarm(
//        @Header("Authorization") token: String,
//        @Query("page") page: Int
//    ): Response<GetAlarmResponse>
//
//    @GET("/api/alarms/exhibition")
//    suspend fun getExhibitionAlarm(
//        @Header("Authorization") token: String,
//        @Query("page") page: Int
//    ): Response<GetAlarmResponse>
//
//    @PATCH("/api/alarms/check/{alarmId}")
//    suspend fun patchAlarmChecked(
//        @Header("Authorization") token: String,
//        @Path("alarmId") alarmId: Int
//    ): Response<OnlyMsgResponse>
//}

interface PopupApi {
    @GET("/api/v1/popups/trend")
    suspend fun getTrends(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetPopupResponse

    @GET("/api/v1/popups/{popupId}")
    suspend fun getPopupDetail(
        @Query("popupId") popupId: Int
    ): Response<GetPopupDetailResponse>
}
