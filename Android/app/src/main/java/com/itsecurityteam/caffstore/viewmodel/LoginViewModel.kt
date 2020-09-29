package com.itsecurityteam.caffstore.viewmodel

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.model.ViewResult
import com.itsecurityteam.caffstore.model.responses.UserType
import com.itsecurityteam.caffstore.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private var userService: UserService? = null

    companion object {
        const val LOGIN_REQUEST = 1001
        const val REGISTER_REQUEST = 1002
    }

    var userType: UserType = UserType.User
        private set

    private val networkResult = MutableLiveData<ViewResult?>()
    val networkResultProp: LiveData<ViewResult?>
        get() = networkResult

    init {
        userService = UserService()
    }

    fun resultProcessed() {
        networkResult.postValue(null)
    }

    fun login(name: String, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            networkResult.postValue(ViewResult(LOGIN_REQUEST, true))
            userType = UserType.Admin
            return@launch

            val response = userService!!.login(name, pass).execute()
            delay(2000)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                if (data.isSuccess) { // Ez lehet, hogy nem is kell
                    networkResult.postValue(ViewResult(LOGIN_REQUEST, true))
                    userType = data.type
                    userService?.saveToken(data.token)
                }
            } else if (response.code() == 404) {
                // Username not found
                networkResult.postValue(
                    ViewResult(
                        LOGIN_REQUEST,
                        false,
                        R.string.invalid_user_name
                    )
                )
            } else if (response.code() == 401) {
                // Incorrect password
                networkResult.postValue(ViewResult(LOGIN_REQUEST, false, R.string.invalid_password))
            }
        }
        //TODO: Exception handling
        //networkResult.postValue(ViewResult(false, R.string.error_empty_input))
        //userId = 15
    }

    fun register(name: String, pass: String, email: String) {
        viewModelScope.launch {
            // TODO: A regisztráció megvalósítása
            // Ugyan azon elven, mint a login

            delay(500)
            networkResult.postValue(ViewResult(REGISTER_REQUEST, true))
        }
    }


    fun validateName(name: Editable?) {
        // TODO: név beviteli mező validálása
        // Amennyiben a bemenet érvénytelen, akkor throw AndroidException(int)
        // A fenti konstruktorban az int egy R.string beli elem
    }

    fun validateEmail(email: Editable?) {
        // TODO: email beviteli mező validálása
        // Hasonlóan a fentihez
    }

    fun validatePassword(pass: Editable?) {
        // TODO: jelszó beviteli mező validálása
        // Hasonlóan a fentihez
    }
}