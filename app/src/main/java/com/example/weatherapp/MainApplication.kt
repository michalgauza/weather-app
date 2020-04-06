package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.di.restApiModule
import com.example.weatherapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(listOf(viewModelModule, restApiModule))
        }
    }
}