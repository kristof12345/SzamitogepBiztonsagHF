package com.itsecurityteam.caffstore.viewmodel

import android.app.Application
import android.text.Editable
import android.util.AndroidException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.itsecurityteam.caffstore.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.RuntimeException

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    data class Result(val success: Boolean, val errorCode: Int = 0)

    private val networkResult = MutableLiveData<Result>()
    val NetworkResult: LiveData<Result>
            get() = networkResult


    fun login(name: String, pass: String) {
        // https://developer.android.com/kotlin/coroutines
        viewModelScope.launch {
            delay(1000)
            networkResult.postValue(Result(true))
        }
    }

    fun register(name: String, pass: String, email: String) {
        viewModelScope.launch {
            delay(1000)
            networkResult.postValue(Result(false, R.string.error_empty_input))
        }
    }


    fun validateName(name: Editable?) {

    }

    fun validateEmail(email: Editable?) {

    }

    fun validatePassword(pass: Editable?) {

    }
}