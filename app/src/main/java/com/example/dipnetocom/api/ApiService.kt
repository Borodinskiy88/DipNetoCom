package com.example.dipnetocom.api

import com.example.dipnetocom.dto.Media
import com.example.dipnetocom.dto.Post
import com.example.dipnetocom.dto.PushToken
import com.example.dipnetocom.model.AuthModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    // Posts

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @GET("posts/latest")
    suspend fun getPostsLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("posts/{post_id}/before")
    suspend fun getPostsBefore(
        @Path("post_id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/{post_id}/after")
    suspend fun getPostsAfter(
        @Path("post_id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @DELETE("posts/{post_id}")
    suspend fun deletePost(@Path("post_id") id: Int): Response<Unit>

    @POST("posts/{post_id}/likes")
    suspend fun likePostById(@Path("post_id") id: Int): Response<Post>

    @DELETE("posts/{post_id}/likes")
    suspend fun dislikePostById(@Path("post_id") id: Int): Response<Post>

    //Media

    @Multipart
    @POST("media")
    suspend fun uploadMedia(@Part part: MultipartBody.Part): Response<Media>

}