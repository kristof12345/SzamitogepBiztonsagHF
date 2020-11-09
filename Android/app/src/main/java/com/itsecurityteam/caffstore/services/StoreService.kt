package com.itsecurityteam.caffstore.services

import com.itsecurityteam.caffstore.model.Comment
import com.itsecurityteam.caffstore.model.filter.Filter
import com.itsecurityteam.caffstore.model.responses.CaffResponse
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream


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
        return http.search(filter.creator, filter.title, filter.free, filter.bought)
    }

    fun loadComments(token: String, caffId: Long): Call<List<Comment>> {
        return http.getComments(token, caffId)
    }

    fun buy(token: String, caffId: Long?): Call<Void> {
        return http.buy(token, caffId!!)
    }

    fun addComment(token: String, caffId: Long?, text: String): Call<Void> {
        return http.comment(token, caffId!!, text)
    }

    fun uploadCaff(token: String, name: String, price: Double, file: File): Call<CaffResponse> {
        val nameData: RequestBody = RequestBody.create(
            MediaType.parse("text/plain"),
            name
        )

        val priceData: RequestBody = RequestBody.create(
            MediaType.parse("text/plain"),
            price.toString()
        )

        val imageData: RequestBody = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            file
        )

        return http.uploadImage(token, nameData, priceData, imageData)
    }

    fun downloadCaff(token: String, id: Long, uri: String) {
        var response = http.downloadImage(token, id).execute()
        try {
            var fileOutputStream = FileOutputStream(File(uri))
            fileOutputStream.write(response.body().bytes())
        } catch (ex: Exception) {
        }
    }
}
