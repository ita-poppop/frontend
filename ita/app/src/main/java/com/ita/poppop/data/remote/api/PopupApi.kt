package com.ita.poppop.data.remote.api

import com.ita.poppop.data.remote.dto.GetPopupDetailResponse
import com.ita.poppop.data.remote.dto.GetPlannedResponse
import com.ita.poppop.data.remote.dto.GetTrendResponse
import retrofit2.Response
import retrofit2.http.GET
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
    ): Response<GetTrendResponse>

    @GET("/api/v1/popups/{popupId}")
    suspend fun getPopupDetail(
        @Query("popupId") popupId: Int
    ): Response<GetPopupDetailResponse>


    @GET("/api/v1/popups/planned")
    suspend fun getPlanned(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<GetPlannedResponse>

}

//interface PopupApi {
//    @GET("/api/v1/popups/trend")
//    suspend fun getTrends(
//        @Query("page") page: Int,
//        @Query("size") size: Int
//    ): GetTrendResponse
//}
