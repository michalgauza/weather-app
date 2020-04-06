package com.example.weatherapp.net

import com.example.weatherapp.net.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val WEATHER_API_KEY = "ab310a258f27682168646b0d3784225f"

interface RestApi {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double = 51.51,
        @Query("lon") lon: Double = -0.13,
        @Query("appid") apiKey: String = WEATHER_API_KEY
    ): Response<WeatherResponse>
}