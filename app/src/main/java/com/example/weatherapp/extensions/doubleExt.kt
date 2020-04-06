package com.example.weatherapp.extensions

const val DOT = "."
const val COMA = ","
const val CELSIUS_DEGREE = "°C"
const val TEMP_FORMAT = "%.0f"

fun Double.toTwoPlaces() = String.format(TEMP_FORMAT, this).replace(COMA, DOT)

fun Double.toFormattedTemp() = "${this.toTwoPlaces()} $CELSIUS_DEGREE"

