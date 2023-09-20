package com.droidgpt.data.database

import com.droidgpt.data.TimeFormats
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {

    private val formatter : DateTimeFormatter = TimeFormats.DATE_TIME


    override fun write(out: JsonWriter?, value: LocalDateTime?) {
        out?.value(formatter.format(value))
    }

    override fun read(`in`: JsonReader?): LocalDateTime {
        return LocalDateTime.parse(`in`?.nextString(), formatter)
    }

}