package com.example.dipnetocom.api

import com.example.dipnetocom.dto.Event
import com.example.dipnetocom.dto.Job
import com.example.dipnetocom.dto.Media
import com.example.dipnetocom.dto.Post
import com.example.dipnetocom.dto.PushToken
import com.example.dipnetocom.dto.User
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

    //Jobs
    @GET("{job_id}/jobs")
    suspend fun getJobsByUserId(@Path("job_id") id: Int): Response<List<Job>>

    @POST("my/jobs")
    suspend fun saveJob(@Body job: Job): Response<Job>

    @DELETE("my/jobs/{job_id}")
    suspend fun deleteJobById(@Path("job_id") id: Int): Response<Unit>

    //Events
    @GET("events")
    suspend fun getAllEvents(): Response<List<Event>>

    @POST("events")
    suspend fun saveEvents(@Body event: Event): Response<Event>

    @GET("events/latest")
    suspend fun getEventsLatest(@Query("count") count: Int): Response<List<Event>>

    @GET("events/{event_id}/before")
    suspend fun getEventsBefore(
        @Path("event_id") id: Int,
        @Query("count") count: Int
    ): Response<List<Event>>

    @GET("events/{event_id}/after")
    suspend fun getEventsAfter(
        @Path("event_id") id: Int,
        @Query("count") count: Int
    ): Response<List<Event>>

    @DELETE("events/{event_id}")
    suspend fun deleteEvent(@Path("event_id") id: Int): Response<Unit>

    @POST("events/{event_id}/likes")
    suspend fun likeEventById(@Path("event_id") id: Int): Response<Event>

    @DELETE("events/{event_id}/likes")
    suspend fun dislikeEventById(@Path("event_id") id: Int): Response<Event>

    // Posts
    @GET("posts")
    suspend fun getAllPosts(): Response<List<Post>>

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

    //User
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>

    //Wall

    @GET("{author_id}/wall/latest")
    suspend fun wallGetLatest(
        @Path("author_id") authorId: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("{author_id}/wall/{post_id}/before")
    suspend fun wallGetBefore(
        @Path("author_id") authorId: Int,
        @Path("post_id") postId: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

}