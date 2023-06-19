package com.example.dipnetocom.repository

import com.example.dipnetocom.dto.User

interface UserRepository {
    suspend fun getUserById(id: Int): User
}