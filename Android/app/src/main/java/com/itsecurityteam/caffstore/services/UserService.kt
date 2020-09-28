package com.itsecurityteam.caffstore.services

import com.itsecurityteam.caffstore.model.requests.LoginRequest
import com.itsecurityteam.caffstore.model.responses.LoginResponse
import retrofit2.Response
import retrofit2.Retrofit

class UserService() {
    private val baseUrl = "https://example.com/" //TODO: set base url
    private val http: HttpService
    private var token: String? = null

    init {
        this.http = Retrofit.Builder().baseUrl(baseUrl).build().create(HttpService::class.java)
    }

    fun login(username: String, password: String): Response<LoginResponse> {
        val request = LoginRequest(username, password)
        return http.loginUser(request)
    }

    fun saveToken(token: String?) {
        this.token = token //TODO: ezt be lehetne állítani a retrofitben is, csak akkor ugyanaz a példány kell minden service-be
    }
}