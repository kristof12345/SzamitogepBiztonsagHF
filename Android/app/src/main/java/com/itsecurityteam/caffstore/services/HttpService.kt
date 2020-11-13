package com.itsecurityteam.caffstore.services

import com.itsecurityteam.caffstore.model.requests.LoginRequest
import com.itsecurityteam.caffstore.model.requests.RegisterRequest
import com.itsecurityteam.caffstore.model.responses.CaffResponse
import com.itsecurityteam.caffstore.model.responses.CommentResponse
import com.itsecurityteam.caffstore.model.responses.LoginResponse
import okhttp3.MultipartBody
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
    fun getComments(@Header("Authorization") token: String, @Path("id") id: Long): Call<List<CommentResponse>>

    @POST("caffs/{id}/buy")
    fun buy(@Header("Authorization") token: String, @Path("id") id: Long): Call<Void>

    @POST("caffs/{id}/comments")
    fun comment(@Header("Authorization") token: String, @Path("id") id: Long, @Body text: String): Call<ResponseBody>

    @Multipart
    @POST("caffs/{name}/{price}")
    fun uploadImage(@Header("Authorization") token: String, @Path("name") name: String, @Path("price") price: String, @Part file: MultipartBody.Part): Call<CaffResponse>

    @GET("caffs/{id}/download")
    fun downloadImage(@Header("Authorization") token: String, @Path("id") id: Long): Call<ResponseBody>

    @DELETE("caffs/{id}")
    fun deleteCaff(@Header("Authorization") token: String, @Path("id") id: Long): Call<ResponseBody>

    @DELETE("caffs/{caffId}/comments/{commentId}")
    fun deleteComment(@Header("Authorization") token: String, @Path("caffId") caffId: Long, @Path("commentId") commentId: Long): Call<ResponseBody>
}