package com.itsecurityteam.caffstore.viewmodel

import android.app.Application
import android.text.Editable
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.model.ViewResult
import com.itsecurityteam.caffstore.model.responses.UserType
import com.itsecurityteam.caffstore.CaffStoreApplication
import com.itsecurityteam.caffstore.exceptions.AndroidException
import com.itsecurityteam.caffstore.services.SessionManager
import com.itsecurityteam.caffstore.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val userService: UserService = UserService()
    private val sessionManager: SessionManager = SessionManager(CaffStoreApplication.appContext)

    companion object {
        const val LOGIN_REQUEST = 1001
        const val REGISTER_REQUEST = 1002
    }

    var userType: UserType = UserType.User
        private set

    private val networkResult = MutableLiveData<ViewResult?>()
    val networkResultProp: LiveData<ViewResult?>
        get() = networkResult

    fun resultProcessed() {
        networkResult.postValue(null)
    }

    fun login(name: String, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userService.login(name, pass).execute()
                delay(2000)
                when {
                    response.isSuccessful -> {
                        val data = response.body()
                        networkResult.postValue(ViewResult(LOGIN_REQUEST, true))
                        userType = data.type
                        sessionManager.saveAuthToken(data.token!!)
                    }
                    response.code() == 404 -> {
                        // Username not found
                        networkResult.postValue(
                            ViewResult(
                                LOGIN_REQUEST,
                                false,
                                R.string.invalid_user_name
                            )
                        )
                    }
                    response.code() == 401 -> {
                        // Incorrect password
                        networkResult.postValue(
                            ViewResult(
                                LOGIN_REQUEST,
                                false,
                                R.string.invalid_password
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                networkResult.postValue(
                    ViewResult(
                        LOGIN_REQUEST,
                        false,
                        R.string.network_error
                    )
                )
            }
        }
    }

    fun register(name: String, pass: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userService.register(name, pass, email).execute()
                delay(2000)
                when {
                    response.isSuccessful -> {
                        networkResult.postValue(ViewResult(REGISTER_REQUEST, true))
                    }
                    response.code() == 409 -> {
                        // Duplicate username
                        networkResult.postValue(
                            ViewResult(
                                REGISTER_REQUEST,
                                false,
                                R.string.duplicate_user_name
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                networkResult.postValue(
                    ViewResult(
                        REGISTER_REQUEST,
                        false,
                        R.string.network_error
                    )
                )
            }
        }
    }


    fun validateName(name: Editable?) {
        // TODO: név beviteli mező validálása
        // Amennyiben a bemenet érvénytelen, akkor throw AndroidException(int)
        // A fenti konstruktorban az int egy R.string beli elem
    }

    fun validateEmail(email: Editable?) {
        val text = email.toString()
        if(text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(text).matches()){
            throw AndroidException(R.string.invalid_email_address)
        }
    }

    fun validatePassword(pass: Editable?) {
        // TODO: jelszó beviteli mező validálása
        // Hasonlóan a fentihez
    }
}