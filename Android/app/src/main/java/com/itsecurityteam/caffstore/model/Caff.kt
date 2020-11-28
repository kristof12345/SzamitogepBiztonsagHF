package com.itsecurityteam.caffstore.model

import android.graphics.Bitmap
import java.time.LocalDateTime

data class Caff(
    val id: Long,
    val name: String,
    val creationDate: LocalDateTime,
    val creator: String,
    val duration: Int,
    val thumbnail: Bitmap,
    val cost: Double,
    var bought: Boolean,
    var url: String?,
    var image: Bitmap? = null
)