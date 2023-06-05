package com.example.dipnetocom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dipnetocom.auth.AppAuth
import com.example.dipnetocom.error.ApiError
import com.example.dipnetocom.model.AuthModelState
import com.example.dipnetocom.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    private val _state = MutableLiveData<AuthModelState>()
    val state: LiveData<AuthModelState>
        get() = _state

    fun login(login: String, password: String) = viewModelScope.launch {
        if (login.isNotBlank() && password.isNotBlank()) {
            try {
                _state.value = (AuthModelState(loading = true))
                val result = repository.login(login, password)
                appAuth.setAuth(result.id, result.token)
                _state.value = AuthModelState(successfulEntry = true)
            } catch (e: Exception) {
                when (e) {
                    is ApiError -> if (e.status == 404) _state.value =
                        AuthModelState(invalidLoginOrPassword = true)

                    else -> _state.value = AuthModelState(error = true)
                }
            }
        } else {
            _state.value = AuthModelState(isBlank = true)
        }
        _state.value = AuthModelState()
    }
}