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
    companion object {
        const val LOGIN_REQUEST = 1001
        const val REGISTER_REQUEST = 1002
    }

    var UserId: Long = -1

    private val networkResult = MutableLiveData<ViewResult?>()
    val NetworkResult: LiveData<ViewResult?>
            get() = networkResult

    fun resultProcessed() {
        networkResult.postValue(null)
    }

    fun login(name: String, pass: String) {
        viewModelScope.launch {
            // TODO: A bejelentkezés megvalósítása
            // Maga a hálózati hivást nem itt, hanem a service-ben egy suspend fun-ban
            // https://developer.android.com/kotlin/coroutines
            // Az eredményt a networkResult.postValue-val lehet jelenteni
            // Paramétere ViewResult(REQ azonosító (company object),sikeres-e, hiba kód, amely egy R.string-re mutat)
            // Fontos, hogy eredményben kell kapni egy UserID-t, az itt be kell állítani
            // FONTOS A viewModelScope-ban eldobott hibák a UI-ban nem jelennek meg, szóval nem kezelem le őket.
            // ha hiba van (és nincs lekezelve), akkor azt a networkResult-ban kell jelezni

            // TODO: 2 sec-es timeout nem árt bele, mert a UI úgy nem zavaró (meg amúgy is)
            // PL.:
            delay(500)
            //networkResult.postValue(ViewResult(false, R.string.error_empty_input))
            UserId = 15
            networkResult.postValue(ViewResult(LOGIN_REQUEST,true))
        }
    }

    fun register(name: String, pass: String, email: String) {
        viewModelScope.launch {
            // TODO: A regisztráció megvalósítása
            // Ugyan azon elven, mint a login

            delay(500)
            networkResult.postValue(ViewResult(REGISTER_REQUEST,true))
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