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

    var bought: Boolean = false
        private set

    var free: Boolean = false
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
        viewModelScope.launch {
            val response =
                storeService.loadCaffs(sessionManager.fetchAuthToken()!!, filter).execute()
            when {
                response.isSuccessful -> {
                    val list = response.body()
                    var caffsList = ArrayList<Caff>()
                    for (s in sort(list, orderBy, orderDir)) {
                        caffsList.add(
                            Caff(
                                s.id, s.name, s.creationDate, s.creator, s.duration,
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

    /**
     *  Egy képet tölt le egy bitmapbe a megadott URL-ről
     */
    private suspend fun loadImage(urlText: String): Bitmap {
        return withContext(Dispatchers.IO) {
            val url = URL(urlText)
            return@withContext BitmapFactory.decodeStream(url.openConnection().getInputStream())
        }
    }

    fun select(caff: Caff) {
        selectedCaff.value = caff
        comments.postValue(emptyList())

        viewModelScope.launch {
            val response =
                storeService.loadComments(sessionManager.fetchAuthToken()!!, caff.id).execute()
            when {
                response.isSuccessful -> {
                    val commentList = response.body()
                    comments.postValue(commentList)
                }
            }

            caff.image = loadImage(caff.url)
            selectedCaff.postValue(caff)
        }
    }


    fun resultProcessed() {
        result.postValue(null)
    }

    fun buy() {
        viewModelScope.launch {
            // TODO: Termék megvásárlásának beállítása
            //  Fontos, hogy a jelenleg kiválasztottat és a listában lévőt is frissíteni kell
            // Ua, mint a LoginViewModel-ben a login/register
            val response = storeService.buy(sessionManager.fetchAuthToken()!!, selectedCaff.value?.id).execute()
            when {
                response.isSuccessful -> {
                    val selected = selectedCaff.value
                    selected?.let {
                        it.bought = true
                        selectedCaff.postValue(it)
                        result.postValue(ViewResult(BUY_REQUEST, true))
                    }
                }
                else -> {
                    result.postValue(ViewResult(BUY_REQUEST, false, R.string.purchase_error))
                }
            }
        }
    }

    fun addComment(text: String) {
        viewModelScope.launch {
            // TODO: Komment hozzáadásanak megvalósítása
            // Ua, mint a LoginViewModel-ben a login/register
            delay(500)
            result.postValue(ViewResult(ADD_COMMENT_REQUEST, true))
        }
    }

    fun uploadCaff(name: String, price: Double, uri: Uri) {
        viewModelScope.launch {
            // TODO: Upload megvalósítása
            // Ua, mint a LoginViewModel-ben a login/register

            // TODO: URI ellenőrzés. Az létezik, lehet belőle olvasni is, de mivel ITSec házi,
            //  valahogy nézni kéne, hogy értelmes-e a kiterjesztés legalább
            //  Ha unatkozol, akkor a name-t is
            delay(500)
            result.postValue(ViewResult(UPLOAD_REQUEST, true))
        }
    }

    fun downloadCaff(uri: Uri) {
        viewModelScope.launch {
            // TODO: Upload megvalósítása
            // Ua, mint a LoginViewModel-ben a login/register

            // TODO: URI ellenőrzés. Az létezik, lehet belőle olvasni is, de mivel ITSec házi,
            //  valahogy nézni kéne, hogy értelmes-e a kiterjesztés legalább
            delay(500)
            result.postValue(ViewResult(DOWNLOAD_REQUEST, true))
        }
    }

    fun removeCurrentCaff() {
        viewModelScope.launch {
            delay(500)
            result.postValue(ViewResult(REMOVE_CAFF_REQUEST, true))
        }
    }

    fun removeComment(comment: Comment) {
        viewModelScope.launch {
            delay(500)
            result.postValue(ViewResult(REMOVE_COMMENT_REQUEST, true))
        }
    }

    fun setOrdering(orderBy: OrderBy, orderDir: OrderDirection) {
        this.orderDir = orderDir
        this.orderBy = orderBy

        // TODO: Vagy loadDatabase, vagy csak szűrés a jelenlegin
    }

    fun setFilter(name: String, creator: String) {
        this.filter.title = name
        this.filter.creator = creator

        // TODO: Vagy loadDatabase, vagy csak szűrés a jelenlegin
    }

    fun setCheckbox(free: Boolean, bought: Boolean) {
        this.free = free
        this.bought = bought

        // TODO: Vagy loadDatabase, vagy csak szűrés a jelenlegin
    }
}
