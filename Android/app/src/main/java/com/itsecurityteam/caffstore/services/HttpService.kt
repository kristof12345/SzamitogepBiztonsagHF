package com.itsecurityteam.caffstore.services

import com.itsecurityteam.caffstore.model.Comment
import com.itsecurityteam.caffstore.model.requests.LoginRequest
import com.itsecurityteam.caffstore.model.requests.RegisterRequest
import com.itsecurityteam.caffstore.model.responses.CaffResponse
import com.itsecurityteam.caffstore.model.responses.LoginResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface HttpService {
    @PUT("users")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("users")
    fun registerUser(@Body request: RegisterRequest): Call<Void>

    @GET("caffs")
    fun search(
        @Header("Authorization") token: String,
        @Query("creator") creator: String?,
        @Query("title") title: String?,
        @Query("free") free: Boolean?,
        @Query("bought") bought: Boolean?
    ): Call<List<CaffResponse>>

    @GET("caffs/{id}/comments")
    fun getComments(@Header("Authorization") token: String, @Path("id") id: Long): Call<List<Comment>>

    @POST("caffs/{id}/buy")
    fun buy(@Header("Authorization") token: String, @Path("id") id: Long): Call<Void>

    @POST("caffs/{id}/comments")
    fun comment(@Header("Authorization") token: String, @Path("id") id: Long, @Body text: String): Call<Void>

    @Multipart
    @POST("caffs")
    fun uploadImage(@Header("Authorization") token: String, @Part("name") name: RequestBody, @Part("name") price: RequestBody, @Part("image") file: RequestBody): Call<CaffResponse>

    @GET("caffs/{id}/download")
    fun downloadImage(@Header("Authorization") token: String, @Path("id") id: Long): Call<ResponseBody>
}