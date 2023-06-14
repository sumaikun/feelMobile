package com.jvega.feel.api

import com.jvega.feel.models.LoginResponse
import com.jvega.feel.models.ValidationResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("/api/login/")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("/api/validate-token/{token}/")
    suspend fun validateToken(@Path("token") token: String): Response<ValidationResponse>

}
