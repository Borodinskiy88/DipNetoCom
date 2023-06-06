package com.example.dipnetocom.entity

import androidx.room.TypeConverter
import com.example.dipnetocom.dto.Coordinates
import com.example.dipnetocom.dto.UserPreview
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListIntConverter {
    @TypeConverter
    fun toJson(list: List<Int>) = Gson().toJson(list)

    @TypeConverter
    fun fromJson(json: String): List<Int> {
        return Gson().fromJson(json, object : TypeToken<List<Int>>() {}.type)
    }
}

class MapUsersPrevConverter {
    @TypeConverter
    fun toJson(map: Map<Int, UserPreview>) = Gson().toJson(map)

    @TypeConverter
    fun fromJson(json: String): Map<Int, UserPreview> {
        return Gson().fromJson(json, object : TypeToken<Map<Int, UserPreview>>() {}.type)
    }
}

class CoordinatesConverter {
    @TypeConverter
    fun toJson(coordinates: Coordinates?) = Gson().toJson(coordinates)

    @TypeConverter
    fun fromJson(json: String): Coordinates? {
        return Gson().fromJson(json, object : TypeToken<Coordinates>() {}.type)
    }
}