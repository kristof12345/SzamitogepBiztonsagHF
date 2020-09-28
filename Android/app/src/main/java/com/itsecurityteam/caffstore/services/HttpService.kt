package com.itsecurityteam.caffstore.services

import com.itsecurityteam.caffstore.model.requests.LoginRequest
import com.itsecurityteam.caffstore.model.requests.RegisterRequest
import com.itsecurityteam.caffstore.model.responses.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface HttpService {
    @PUT("users/login")
    fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("users/register")
    fun createUser(@Body request: RegisterRequest): Response<Void>
}