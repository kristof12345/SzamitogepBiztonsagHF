package com.itsecurityteam.caffstore.services

import com.itsecurityteam.caffstore.model.Comment
import com.itsecurityteam.caffstore.model.requests.LoginRequest
import com.itsecurityteam.caffstore.model.requests.RegisterRequest
import com.itsecurityteam.caffstore.model.responses.CaffResponse
import com.itsecurityteam.caffstore.model.responses.LoginResponse
import retrofit2.Call
import retrofit2.http.*


interface HttpService {
    @PUT("users")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("users")
    fun registerUser(@Body request: RegisterRequest): Call<Void>

    @GET("caffs")
    fun search(@Header("Authorization") token: String, @Query("creator") creator: String?, @Query("title") title: String?): Call<List<CaffResponse>>

    @GET("caffs/{id}/comments")
    fun getComments(@Header("Authorization") token: String, @Path("id") id: Long): Call<List<Comment>>

    @POST("caffs/{id}/buy")
    fun buy(@Header("Authorization") token: String, @Path("id") id: Long): Call<Void>
}