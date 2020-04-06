package com.example.weatherapp.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.extensions.SingleLiveEvent
import com.example.weatherapp.net.ResponseWrapper
import com.example.weatherapp.net.WeatherRepository
import com.example.weatherapp.net.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import retrofit2.Response

class MainActivityViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    val weatherLiveData = MutableLiveData<ResponseWrapper<Response<WeatherResponse>>>(null)
    val fetchWeatherInterval = SingleLiveEvent<Boolean>()

    fun getWeather(lon: Double, lat: Double) {
        viewModelScope.launch {
            weatherLiveData.postValue(weatherRepository.getWeather(lon, lat))
        }
    }

    fun intervalUpdate() {
        viewModelScope.launch {
            fetchWeatherInterval.value = true
            delay(600000)
            intervalUpdate()
        }
    }


}