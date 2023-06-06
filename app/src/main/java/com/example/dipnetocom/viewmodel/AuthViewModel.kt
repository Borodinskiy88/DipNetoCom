package com.example.dipnetocom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.dipnetocom.auth.AppAuth
import com.example.dipnetocom.model.AuthModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {

    val data: LiveData<AuthModel?> = appAuth
        .authState
        .asLiveData(Dispatchers.Default)

    //TODO
    val authorized: Boolean
        get() = data.value != null

}