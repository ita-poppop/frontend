package com.ita.poppop.data.remote.repository.profile

import com.ita.poppop.data.remote.api.ProfileApi
import com.ita.poppop.data.remote.dto.profile.GetProfileResponse
import com.ita.poppop.data.remote.dto.profile.PostProfileResponse
import okhttp3.MultipartBody
import retrofit2.HttpException
import retrofit2.Response

class ProfileRepositoryImpl(
    private val api: ProfileApi
) : ProfileRepository {


    override suspend fun postProfile(
        accessToken: String,
        profileImage: MultipartBody.Part?,
        userName: String
    ): Response<PostProfileResponse> {
        try {
            val response = api.postProfile("Bearer $accessToken",profileImage,userName)

            if (response.code() == 200) {
                return response
            } else {
                throw Exception("API 응답 오류: ${response.message()} (코드: ${response.code()})")
            }
        } catch (e: HttpException) {
            // HTTP 500 에러 등을 명확하게 전달
            throw Exception("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getProfile(
        accessToken: String,
        page: Int,
        size: Int
    ): Response<GetProfileResponse> {
        try {
            val response = api.getProfile("Bearer $accessToken",page,size)

            if (response.code() == 200) {
                return response
            } else {
                throw Exception("API 응답 오류: ${response.message()} (코드: ${response.code()})")
            }
        } catch (e: HttpException) {
            // HTTP 500 에러 등을 명확하게 전달
            throw Exception("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }
}