package com.droidgpt.data.database

import com.droidgpt.data.TimeFormats
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateAdapter : TypeAdapter<LocalDate>() {

    private val formatter : DateTimeFormatter = TimeFormats.DATE

    override fun write(out: JsonWriter?, value: LocalDate?) {
        out?.value(formatter.format(value))
    }

    override fun read(`in`: JsonReader?): LocalDate {
        return LocalDate.parse(`in`?.nextString(), formatter)
    }

}