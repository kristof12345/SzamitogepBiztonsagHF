package com.itsecurityteam.caffstore.viewmodel

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.model.ViewResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val networkResult = MutableLiveData<ViewResult>()
    val NetworkResult: LiveData<ViewResult>
            get() = networkResult


    fun login(name: String, pass: String) {
        // https://developer.android.com/kotlin/coroutines
        // TODO: 2s-es timeout legyen, mert úgy néz ki jól a UI (és nem kell töltő karika)
        viewModelScope.launch {
            delay(500)
            //networkResult.postValue(ViewResult(false, R.string.error_empty_input))
            networkResult.postValue(ViewResult(true))
        }
    }

    fun register(name: String, pass: String, email: String) {
        viewModelScope.launch {
            delay(500)
            networkResult.postValue(ViewResult(true))
        }
    }


    fun validateName(name: Editable?) {

    }

    fun validateEmail(email: Editable?) {

    }

    fun validatePassword(pass: Editable?) {

    }
}