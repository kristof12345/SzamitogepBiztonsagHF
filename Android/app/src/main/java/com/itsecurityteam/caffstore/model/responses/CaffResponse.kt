package com.itsecurityteam.caffstore.model.responses

import java.time.LocalDateTime

data class CaffResponse(
    val id: Long,
    val name: String,
    val creationDate: LocalDateTime,
    val creator: String,
    val duration: Int,
    val thumbnailUrl: String,
    val cost: Double,
    var bought: Boolean,
    var imageUrl: String
)