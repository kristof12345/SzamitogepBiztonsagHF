package com.itsecurityteam.caffstore.model

import android.graphics.Bitmap
import java.time.LocalDateTime

data class Caff(
    val id: Long,
    val name: String,
    val creationDate: LocalDateTime,
    val creator: String,
    val length: Int,
    val thumbnail: Bitmap,
    val cost: Double,
    val bought: Boolean,
    var image: Bitmap? = null
)