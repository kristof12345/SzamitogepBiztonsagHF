package com.itsecurityteam.caffstore.model.responses

class LoginResponse {
    var isSuccess: Boolean = false
    var userId: Long = 0
    var token: String? = null
    var type: UserType = UserType.User
}