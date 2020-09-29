package com.itsecurityteam.caffstore.model

import java.time.LocalDateTime

data class Comment(
    val id: Long,
    val userName: String,
    val addTime: LocalDateTime,
    val text: String
)