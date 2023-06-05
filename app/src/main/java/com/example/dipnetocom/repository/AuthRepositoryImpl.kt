package com.example.dipnetocom.repository

import com.example.dipnetocom.api.ApiService
import com.example.dipnetocom.error.ApiError
import com.example.dipnetocom.error.NetworkError
import com.example.dipnetocom.model.AuthModel
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun login(login: String, password: String): AuthModel {
        try {
            val response = apiService.login(login, password)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            return AuthModel(body.id, body.token)

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun register(login: String, password: String, name: String): AuthModel {
        try {
            val response = apiService.register(login, password, name)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            return AuthModel(body.id, body.token)

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

}