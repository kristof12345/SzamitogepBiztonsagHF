package com.itsecurityteam.caffstore.model.responses

data class CaffResponse(
    val id: Long,
    val name: String,
    val creationDate: String,
    val creator: String,
    val duration: Int,
    val thumbnailUrl: String,
    val cost: Double,
    var bought: Boolean,
    var imageUrl: String
)