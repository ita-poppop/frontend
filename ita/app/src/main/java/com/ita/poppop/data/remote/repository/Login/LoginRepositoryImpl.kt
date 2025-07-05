package com.ita.poppop.data.remote.repository.Login

import com.ita.poppop.data.remote.api.LoginApi
import com.ita.poppop.data.remote.dto.GetLoginResponse
import retrofit2.HttpException
import retrofit2.Response


class LoginRepositoryImpl(
    private val api: LoginApi
) : LoginRepository {

    override suspend fun getLoginDetail(
        loginId: Int
    ): Response<GetLoginResponse> {
        try {
            val response = api.getLogin(loginId)

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
