package com.itsecurityteam.caffstore.model.responses

import com.google.gson.annotations.SerializedName

enum class UserType {
    @SerializedName("0")
    User,

    @SerializedName("1")
    Admin
}