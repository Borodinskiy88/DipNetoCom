package com.example.dipnetocom.model

data class AuthModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val isBlank: Boolean = false,
    val successfulEntry: Boolean = false,
    val failedEntry: Boolean = false,
    val invalidLoginOrPassword: Boolean = false
)
