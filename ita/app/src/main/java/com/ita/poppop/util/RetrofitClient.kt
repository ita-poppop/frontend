package com.ita.poppop.util

import com.ita.poppop.data.remote.api.PopupApi
import com.ita.poppop.data.remote.api.ReviewApi
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://43.200.189.197:8080/"

    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val popupApi: PopupApi by lazy {
        retrofit.create(PopupApi::class.java)
    }
    val reviewApi: ReviewApi by lazy {
        retrofit.create(ReviewApi::class.java)
    }

}
