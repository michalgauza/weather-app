package com.example.weatherapp.net.model


import com.google.gson.annotations.SerializedName

const val KELVIN = 273.15

data class Main(
    val temp: Double ,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double
){
    fun getTempInCelsius() = temp - KELVIN
}