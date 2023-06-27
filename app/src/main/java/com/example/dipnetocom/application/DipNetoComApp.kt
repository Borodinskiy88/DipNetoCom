package com.example.dipnetocom.application

import android.app.Application
import com.example.dipnetocom.BuildConfig
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DipNetoComApp : Application() {

    private val MAPKIT_API_KEY = BuildConfig.API_KEY
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
    }
}
