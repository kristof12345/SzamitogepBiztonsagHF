package com.itsecurityteam.caffstore.converter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.util.*

class DotNetDateConverter : JsonDeserializer<Date?> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        val s = json.asJsonPrimitive.asString
        val l = s.substring(s.indexOf("(") + 1, s.indexOf("+")).toLong()
        return Date(l)
    }
}