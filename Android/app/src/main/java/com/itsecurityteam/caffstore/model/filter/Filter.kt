package com.itsecurityteam.caffstore.model.filter

data class Filter(
    var title: String = "",
    var creator: String = "",
    var bought: Boolean = false,
    var free: Boolean = false
)