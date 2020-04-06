package com.example.weatherapp.net

import com.example.weatherapp.net.model.WeatherResponse
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class WeatherRepository(private val api: RestApi) {

    suspend fun getWeather(lon: Double, lat: Double): ResponseWrapper<Response<WeatherResponse>> =
        safeCall { api.getCurrentWeather(lon = lon, lat = lat) }

    private suspend fun <T> safeCall(apiCall: suspend () -> T): ResponseWrapper<T> {
        return try {
            ResponseWrapper.Success(apiCall.invoke())
        } catch (exception: Exception) {
            when (exception) {
                is IOException -> ResponseWrapper.NetworkError(exception.message)
                is HttpException -> {
                    ResponseWrapper.GenericError(exception.code(), exception.message)
                }
                else -> ResponseWrapper.GenericError(null, exception.message)
            }
        }
    }
}