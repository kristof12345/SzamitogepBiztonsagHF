package com.itsecurityteam.caffstore.services

import com.itsecurityteam.caffstore.model.requests.LoginRequest
import com.itsecurityteam.caffstore.model.requests.RegisterRequest
import com.itsecurityteam.caffstore.model.responses.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface HttpService {
    @PUT("users")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("users")
    fun registerUser(@Body request: RegisterRequest): Call<Void>
}