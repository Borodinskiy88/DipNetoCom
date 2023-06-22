package com.example.dipnetocom.repository

import com.example.dipnetocom.model.AuthModel
import com.example.dipnetocom.model.MediaModel

interface AuthRepository {

    suspend fun login(login: String, password: String): AuthModel
    suspend fun register(login: String, password: String, name: String): AuthModel
    suspend fun registerWithPhoto(
        login: String,
        password: String,
        name: String,
        media: MediaModel
    ): AuthModel
}