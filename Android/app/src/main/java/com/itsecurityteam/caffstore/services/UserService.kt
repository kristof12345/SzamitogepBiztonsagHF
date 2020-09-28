package com.itsecurityteam.caffstore.services

import com.itsecurityteam.caffstore.model.requests.LoginRequest
import com.itsecurityteam.caffstore.model.responses.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserService() {
    private val baseUrl = "https://10.0.2.2:5001/" //TODO: set base url
    private val http: HttpService
    private var token: String? = null

    init {
        val okHttpClient = CertificateProvider.getUnsafeOkHttpClient();

        this.http = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(HttpService::class.java)
    }

    fun login(username: String, password: String): Call<LoginResponse> {
        val request = LoginRequest(username, password)
        return http.loginUser(request)
    }

    fun saveToken(token: String?) {
        this.token = token //TODO: ezt be lehetne állítani a retrofitben is, csak akkor ugyanaz a példány kell minden service-be
    }
}