package com.example.dipnetocom.dto

data class User(
    val id: Int,
    val login: String?,
    val name: String?,
    val avatar: String?
)

data class UserPreview(
    val name: String,
    val avatar: String?
)
