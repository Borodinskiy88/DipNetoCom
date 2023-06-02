package com.example.dipnetocom.api

import com.example.dipnetocom.BuildConfig
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    companion object {
        private const val BASEURL = "${BuildConfig.BASE_URL}/api/"
    }

}