package com.example.weatherapp.net

sealed class ResponseWrapper <out T>{
    data class Success<out T>(val value: T): ResponseWrapper<T>()
    data class GenericError(val code: Int? = null, val message: String?): ResponseWrapper<Nothing>()
    data class NetworkError(val message: String?): ResponseWrapper<Nothing>()
}