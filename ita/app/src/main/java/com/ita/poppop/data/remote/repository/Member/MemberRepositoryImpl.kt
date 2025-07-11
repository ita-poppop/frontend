package com.ita.poppop.data.remote.repository.Member

import com.ita.poppop.data.remote.api.MemberApi
import com.ita.poppop.data.remote.api.SignupRequest
import com.ita.poppop.data.remote.dto.member.GetMemberExistsResponse
import com.ita.poppop.data.remote.dto.member.GetMemberInfoResponse
import com.ita.poppop.data.remote.dto.member.PostLogoutResponse
import com.ita.poppop.data.remote.dto.member.PostRefreshResponse
import com.ita.poppop.data.remote.dto.member.PostSignupResponse
import retrofit2.HttpException
import retrofit2.Response


class MemberRepositoryImpl(
    private val api: MemberApi
) : MemberRepository {
    override suspend fun postSignup(
        signupRequest: SignupRequest
    ): Response<PostSignupResponse> {
        try {
            val response = api.postSignup(signupRequest)

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

//    override suspend fun postRefresh(
//        refreshToken: String
//    ): Response<PostRefreshResponse> {
//        try {
//            val response = api.postRefresh(refreshToken)
//
//            if (response.code() == 200) {
//                return response
//            } else {
//                throw Exception("API 응답 오류: ${response.message()} (코드: ${response.code()})")
//            }
//        } catch (e: HttpException) {
//            // HTTP 500 에러 등을 명확하게 전달
//            throw Exception("HTTP ${e.code()}: ${e.message()}")
//        } catch (e: Exception) {
//            throw e
//        }
//    }

    override suspend fun postLogout(
        accessToken: String
    ): Response<PostLogoutResponse> {
        try {
            val response = api.postLogout(accessToken)

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

    override suspend fun getMemberInfo(
        accessToken: String
    ): Response<GetMemberInfoResponse> {
        try {
            val response = api.getMemberInfo("Bearer $accessToken")

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

//    override suspend fun getMemberExists(
//        email: String
//    ): Response<GetMemberExistsResponse> {
//        try {
//            val response = api.getMemberExists(email)
//
//            if (response.code() == 200) {
//                return response
//            } else {
//                throw Exception("API 응답 오류: ${response.message()} (코드: ${response.code()})")
//            }
//        } catch (e: HttpException) {
//            // HTTP 500 에러 등을 명확하게 전달
//            throw Exception("HTTP ${e.code()}: ${e.message()}")
//        } catch (e: Exception) {
//            throw e
//        }
//    }

}
