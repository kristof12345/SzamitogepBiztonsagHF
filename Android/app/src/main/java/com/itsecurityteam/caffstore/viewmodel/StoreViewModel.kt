package com.itsecurityteam.caffstore.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.itsecurityteam.caffstore.CaffStoreApplication
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.model.Caff
import com.itsecurityteam.caffstore.model.Comment
import com.itsecurityteam.caffstore.model.ViewResult
import com.itsecurityteam.caffstore.model.filter.Filter
import com.itsecurityteam.caffstore.model.filter.OrderBy
import com.itsecurityteam.caffstore.model.filter.OrderDirection
import com.itsecurityteam.caffstore.model.responses.CaffResponse
import com.itsecurityteam.caffstore.model.responses.UserType
import com.itsecurityteam.caffstore.services.SessionManager
import com.itsecurityteam.caffstore.services.StoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StoreViewModel(application: Application) : AndroidViewModel(application) {
    private val storeService: StoreService = StoreService()
    private val sessionManager: SessionManager = SessionManager(CaffStoreApplication.appContext)

    companion object {
        const val UPLOAD_REQUEST = 1003
        const val DOWNLOAD_REQUEST = 1004
        const val ADD_COMMENT_REQUEST = 1005
        const val BUY_REQUEST = 1008
        const val REMOVE_COMMENT_REQUEST = 1101
        const val REMOVE_CAFF_REQUEST = 1102
    }

    private val DATE_TIME_FORMAT = "yyyy. MM. dd. H:mm:ss";

    private val caffs = MutableLiveData<List<Caff>>()
    val caffsProp: LiveData<List<Caff>>
        get() = caffs

    private val selectedCaff = MutableLiveData<Caff?>()
    val selectedCaffProp: LiveData<Caff?>
        get() = selectedCaff

    private val comments = MutableLiveData<List<Comment>>()
    val commentsProp: LiveData<List<Comment>>
        get() = comments

    private val result = MutableLiveData<ViewResult?>()
    val resultProp: LiveData<ViewResult?>
        get() = result

    var user: UserType = UserType.User
        private set

    val filter: Filter = Filter()

    var orderBy: OrderBy = OrderBy.Date
        private set

    var orderDir: OrderDirection = OrderDirection.Descending
        private set

    fun signIn(type: UserType) {
        this.user = type
        search()
    }

    var modalPressed = false

    fun sigOut() {
        caffs.postValue(emptyList())
        comments.postValue(emptyList())
        selectedCaff.postValue(null)
        result.postValue(null)
        user = UserType.User
    }

    private fun search() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = storeService.loadCaffs(sessionManager.fetchAuthToken()!!, filter).execute()
            when {
                response.isSuccessful -> {
                    val list = response.body()
                    var caffsList = ArrayList<Caff>()
                    for (s in sort(list, orderBy, orderDir)) {
                        var date = LocalDateTime.parse(s.creationDate, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
                        caffsList.add(
                            Caff(
                                s.id, s.name, date, s.creator, s.duration,
                                loadImage(s.thumbnailUrl), s.cost, s.bought, s.imageUrl
                            )
                        )
                    }
                    caffs.postValue(caffsList)
                }
            }
        }
    }

    private fun sort(
        list: List<CaffResponse>,
        orderBy: OrderBy,
        direction: OrderDirection
    ): List<CaffResponse> {
        var result = when (orderBy) {
            OrderBy.Creator -> list.sortedBy { it.creator }
            OrderBy.Date -> list.sortedBy { it.creationDate }
            OrderBy.Length -> list.sortedBy { it.duration }
            OrderBy.Name -> list.sortedBy { it.name }
        }

        if (direction == OrderDirection.Descending)
            result = result.reversed()

        return result;
    }

    private suspend fun loadImage(urlText: String?): Bitmap {
        return withContext(Dispatchers.IO) {
            var text = urlText
            if (text == null)
                text = "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png"

            val url = URL(text)
            return@withContext BitmapFactory.decodeStream(url.openConnection().getInputStream())
        }
    }

    fun select(caff: Caff) {
        selectedCaff.value = caff
        comments.postValue(emptyList())

        viewModelScope.launch(Dispatchers.IO) {
            val response = storeService.loadComments(sessionManager.fetchAuthToken()!!, caff.id).execute()
            when {
                response.isSuccessful -> {
                    val list = response.body()
                    var commentList = ArrayList<Comment>()
                    for (s in list) {
                        var date = LocalDateTime.parse(s.addTime, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
                        commentList.add(
                            Comment(
                                s.id, s.userName, date, s.text
                            )
                        )
                    }
                    comments.postValue(commentList)
                }
            }

            caff.image = loadImage(caff?.url)
            selectedCaff.postValue(caff)
        }
    }

    fun resultProcessed() {
        result.postValue(null)
    }

    fun buy() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = storeService.buy(sessionManager.fetchAuthToken()!!, selectedCaff.value?.id).execute()
            when {
                response.isSuccessful -> {
                    val selected = selectedCaff.value
                    selected?.let {
                        it.bought = true
                        selectedCaff.postValue(it)
                        result.postValue(ViewResult(BUY_REQUEST, true))
                    }
                    caffs.value?.let {
                        it.stream().filter { a -> a.id == selected?.id }.forEach { a -> a.bought = true }
                    }
                }
                else -> {
                    result.postValue(ViewResult(BUY_REQUEST, false, R.string.purchase_error))
                }
            }
        }
    }

    fun addComment(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = storeService.addComment(
                sessionManager.fetchAuthToken()!!,
                selectedCaff.value?.id,
                text
            ).execute()
            when {
                response.isSuccessful -> {
                    result.postValue(ViewResult(ADD_COMMENT_REQUEST, true))
                }
                else -> {
                    result.postValue(ViewResult(ADD_COMMENT_REQUEST, false, R.string.comment_error))
                }
            }
        }
    }

    fun uploadCaff(name: String, price: Double, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            var fileUri = uri.path
            if (fileUri == null) {
                result.postValue(ViewResult(UPLOAD_REQUEST, false))
            } else {
                if (fileUri.takeLast(5) != ".caff") {
                    result.postValue(ViewResult(UPLOAD_REQUEST, false))
                } else {
                    var response = storeService.uploadCaff(sessionManager.fetchAuthToken()!!, name, price, uri).execute()

                    when {
                        response.isSuccessful -> {
                            result.postValue(ViewResult(UPLOAD_REQUEST, true))
                        }
                    }
                }
            }
        }
    }

    fun downloadCaff(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            var fileUri = uri.path
            if (fileUri == null)
                result.postValue(ViewResult(DOWNLOAD_REQUEST, false))
            else {
                storeService.downloadCaff(sessionManager.fetchAuthToken()!!, selectedCaff.value?.id!!, fileUri)
                result.postValue(ViewResult(DOWNLOAD_REQUEST, true))
            }
        }
    }

    fun removeCurrentCaff() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            var response = storeService.deleteCaff(sessionManager.fetchAuthToken()!!, selectedCaff.value?.id!!).execute()
            when {
                response.isSuccessful -> {
                    result.postValue(ViewResult(REMOVE_CAFF_REQUEST, true))
                }
                else -> {
                    result.postValue(ViewResult(REMOVE_CAFF_REQUEST, false, R.string.delete_error))
                }
            }
        }
    }

    fun removeComment(comment: Comment) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            var response = storeService.deleteComment(sessionManager.fetchAuthToken()!!, selectedCaff.value?.id!!, comment.id).execute()
            when {
                response.isSuccessful -> {
                    result.postValue(ViewResult(REMOVE_COMMENT_REQUEST, true))
                }
                else -> {
                    result.postValue(ViewResult(REMOVE_COMMENT_REQUEST, false, R.string.delete_comment_error))
                }
            }
        }
    }

    fun setOrdering(orderBy: OrderBy, orderDir: OrderDirection) {
        this.orderDir = orderDir
        this.orderBy = orderBy

        search()
    }

    fun setFilter(name: String, creator: String, isFree: Boolean, isBought: Boolean) {
        this.filter.title = name
        this.filter.creator = creator
        this.filter.free = isFree
        this.filter.bought = isBought
        search()
    }
}