package com.itsecurityteam.caffstore.viewmodel

import android.app.Application
import android.text.Editable
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.itsecurityteam.caffstore.CaffStoreApplication
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.exceptions.AndroidException
import com.itsecurityteam.caffstore.model.ViewResult
import com.itsecurityteam.caffstore.model.responses.UserType
import com.itsecurityteam.caffstore.services.SessionManager
import com.itsecurityteam.caffstore.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

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
                when {
                    response.isSuccessful -> {
                        val data = response.body()
                        userType = data.userType
                        sessionManager.saveAuthToken(data.token!!)
                        networkResult.postValue(ViewResult(LOGIN_REQUEST, true))
                    }
                    response.code() == 401 -> {
                        // Incorrect password
                        networkResult.postValue(
                            ViewResult(
                                LOGIN_REQUEST,
                                false,
                                R.string.invalid_password_or_user
                            )
                        )
                    }
                    else -> throw Exception()
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
                    else -> throw Exception()
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
        if(name == null || name.length < 5)
            throw AndroidException(R.string.tooShortUsername)
    }

    fun validateEmail(email: Editable?) {
        val text = email.toString()
        if(text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(text).matches()){
            throw AndroidException(R.string.invalid_email_address)
        }
    }

    fun validatePassword(pass: Editable?) {
        if(pass == null || pass.length < 7)
            throw AndroidException(R.string.tooShortPassword)

        if(!isValidPasswordFormat(pass.toString()))
            throw AndroidException(R.string.tooWeakPassword)
    }

    private fun isValidPasswordFormat(password: String): Boolean {
        val passwordREGEX = Pattern.compile("^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=!])" +    //at least 1 special character
                ".{7,}" +               //at least 7 characters
                "$")
        return passwordREGEX.matcher(password).matches()
    }
}