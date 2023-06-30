package com.example.dipnetocom.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object ReformatValues {
    fun reformatCount(count: Int): String {
        val formatCount = when {
            count in 1000..9999 -> {
                String.format("%.1fK", count / 1000.0)
            }

            count in 10000..999999 -> {
                String.format("%dK", count / 1000)
            }

            count >= 1000000 -> {
                String.format("%.1fM", count / 1000000.0)
            }
            else -> count.toString()
        }
        return formatCount
    }

    fun reformatWebLink(url: String): String {
        val removeHttps = url.substringAfterLast("https://")
        return removeHttps.substringBeforeLast("/")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun reformatDateTime(date: String): String {
        val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        return parsedDate.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun reformatDate(date: String): String {
        val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return parsedDate.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun reformatTime(date: String): String {
        val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return parsedDate.format(formatter)
    }

    fun reformatDatePicker(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    }

    fun reformatTimePicker(date: Date): String {
        return SimpleDateFormat("HH:mm", Locale.ROOT).format(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun lastDate(datetime: String) = Instant.parse(datetime).toEpochMilli()

}