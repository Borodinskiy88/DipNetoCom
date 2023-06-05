package com.example.dipnetocom.api

import com.example.dipnetocom.dto.PushToken
import com.example.dipnetocom.model.AuthModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    //Auth
    @POST("users/push-tokens")
    suspend fun saveToken(@Body pushToken: PushToken)

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun login(
        @Field("login") login: String,
        @Field("password") password: String
    ): Response<AuthModel>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun register(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): Response<AuthModel>
}