package com.example.dipnetocom.repository

import com.example.dipnetocom.model.AuthModel

interface AuthRepository {

    suspend fun login(login: String, password: String): AuthModel

    suspend fun register(login: String, password: String, name: String): AuthModel
}