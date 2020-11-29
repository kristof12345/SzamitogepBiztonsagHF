package com.itsecurityteam.caffstore.services

import android.net.Uri
import com.google.gson.GsonBuilder
import com.itsecurityteam.caffstore.CaffStoreApplication
import com.itsecurityteam.caffstore.converter.DotNetDateConverter
import com.itsecurityteam.caffstore.model.filter.Filter
import com.itsecurityteam.caffstore.model.requests.CommentRequest
import com.itsecurityteam.caffstore.model.responses.CaffResponse
import com.itsecurityteam.caffstore.model.responses.CommentResponse
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.net.URL
import java.util.*


class StoreService {
    private val baseUrl = "https://10.0.2.2:5001/"
    private val http: HttpService
    private val okHttpClient = CertificateProvider.getUnsafeOkHttpClient()

    init {
        val gson = GsonBuilder().registerTypeAdapter(Date::class.java, DotNetDateConverter()).create()

        this.http = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build().create(HttpService::class.java)
    }

    fun loadCaffs(token: String, filter: Filter): Call<List<CaffResponse>> {
        return http.search(token, filter.creator, filter.title, filter.free, filter.bought)
    }

    fun loadComments(token: String, caffId: Long): Call<List<CommentResponse>> {
        return http.getComments(token, caffId)
    }

    fun buy(token: String, caffId: Long?): Call<Void> {
        return http.buy(token, caffId!!)
    }

    fun addComment(token: String, caffId: Long?, text: String): Call<ResponseBody> {
        val comment = CommentRequest(text)
        return http.comment(token, caffId!!, comment)
    }

    fun uploadCaff(token: String, name: String, price: Double, uri: Uri): Call<CaffResponse> {
        val inputStream: InputStream? = CaffStoreApplication.appContext.contentResolver.openInputStream(uri)
        val file: File = File.createTempFile(name, ".caff")
        file.deleteOnExit()
        val out = FileOutputStream(file)
        copyStream(inputStream!!, out)

        val filePart = MultipartBody.Part.createFormData("file", file.name, RequestBody.create(MediaType.parse("image/*"), file))
        return http.uploadImage(token, name, price.toString(), filePart)
    }

    @Throws(IOException::class)
    fun copyStream(input: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }

    fun downloadCaff(token: String, id: Long, uri: String) {
        val response = http.downloadImage(token, id).execute()
        val fileOutputStream = FileOutputStream(uri)
        fileOutputStream.write(response.body().bytes())
    }

    fun deleteCaff(token: String, id: Long): Call<ResponseBody> {
        return http.deleteCaff(token, id)
    }

    fun deleteComment(token: String, caffId: Long, commentId: Long): Call<ResponseBody> {
        return http.deleteComment(token, caffId, commentId)
    }

    fun loadImage(uri: String?): InputStream {
        val request: Request = Request.Builder().url(uri).build()
        val response = okHttpClient.newCall(request).execute()
        return response.body().byteStream()
    }

    fun loadPlaceholderImage(): InputStream {
        val text = "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png"
        val url = URL(text)
        return url.openConnection().getInputStream()
    }
}
