package com.ita.poppop.util

import com.google.android.gms.common.api.Api
import com.ita.poppop.data.remote.api.MemberApi
import com.ita.poppop.data.remote.api.PopupApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://43.200.189.197:8080"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환
            .build()
    }

    val trendApi: PopupApi by lazy {
        retrofit.create(PopupApi::class.java)
    }

    val memberApi: MemberApi by lazy {
        retrofit.create(MemberApi::class.java)
    }
}
