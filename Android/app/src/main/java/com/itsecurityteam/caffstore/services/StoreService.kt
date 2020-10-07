package com.itsecurityteam.caffstore.services

import com.itsecurityteam.caffstore.model.Comment
import com.itsecurityteam.caffstore.model.filter.Filter
import com.itsecurityteam.caffstore.model.responses.CaffResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StoreService {
    private val baseUrl = "https://10.0.2.2:5001/"
    private val http: HttpService

    init {
        val okHttpClient = CertificateProvider.getUnsafeOkHttpClient();

        this.http = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(HttpService::class.java)
    }

    fun loadCaffs(token: String, filter: Filter): Call<List<CaffResponse>> {
        return http.search(token, filter.creator, filter.title)
    }

    fun loadComments(token: String, caffId: Long): Call<List<Comment>> {
        return http.getComments(token, caffId)
    }

    fun buy(token: String, caffId: Long?): Call<Void> {
        return http.buy(token, caffId!!)
    }
}