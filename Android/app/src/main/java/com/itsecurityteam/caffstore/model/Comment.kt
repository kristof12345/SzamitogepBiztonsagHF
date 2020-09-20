package com.itsecurityteam.caffstore.model

import java.time.LocalDateTime

data class Comment(
    val userName: String,
    val addTime: LocalDateTime,
    val text: String
)