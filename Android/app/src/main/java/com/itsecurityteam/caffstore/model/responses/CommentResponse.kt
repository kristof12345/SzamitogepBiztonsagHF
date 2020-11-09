package com.itsecurityteam.caffstore.model.responses

data class CommentResponse(
    val id: Long,
    val userName: String,
    val addTime: String,
    val text: String
)