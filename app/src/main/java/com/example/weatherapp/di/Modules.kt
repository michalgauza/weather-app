package com.example.weatherapp.di

import com.example.weatherapp.net.RestApi
import com.example.weatherapp.net.WeatherRepository
import com.example.weatherapp.vm.MainActivityViewModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

val viewModelModule = module {
    viewModel { MainActivityViewModel(get()) }
}

val restApiModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideRestApi(get()) }
    single { WeatherRepository(get()) }
}

private fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient().newBuilder().build()
}

private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl("http://api.openweathermap.org/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(Gson().newBuilder().create()))
        .build()
}

private fun provideRestApi(retrofit: Retrofit): RestApi = retrofit.create(RestApi::class.java)
