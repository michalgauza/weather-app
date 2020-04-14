package com.example.weatherapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.vm.MainActivityViewModel
import com.example.weatherapp.R
import com.example.weatherapp.extensions.toFormattedTemp
import com.example.weatherapp.net.ResponseWrapper
import com.example.weatherapp.net.model.WeatherResponse
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val viewModel by inject<MainActivityViewModel>()

    private val permissionsRequestCode = 12
    private val intentScheme = "package"

    private val weatherResponseObserver = Observer<ResponseWrapper<Response<WeatherResponse>>> {
        when (it) {
            is ResponseWrapper.Success -> setupInfo(it.value.body() as WeatherResponse)
            is ResponseWrapper.GenericError -> Toast.makeText(
                this,
                "${getString(R.string.generic_error)}: ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
            is ResponseWrapper.NetworkError -> Toast.makeText(
                this,
                getString(R.string.network_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val fetchWeatherIntervalObserver = Observer<Boolean> {
        updateWeather()
    }
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var lon: Double? = null
    private var lat: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        with(viewModel) {
            weatherLiveData.observe(this@MainActivity, weatherResponseObserver)
            fetchWeatherInterval.observe(this@MainActivity, fetchWeatherIntervalObserver)
            intervalUpdate()
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (!checkPermissions()) requestPermissions()
        else getLastLocation()
        hideSystemUI()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
          window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }


    private fun updateWeather() {
        if (lat != null && lon != null) viewModel.getWeather(lon!!, lat!!)
        else Snackbar.make(
            constraint_layout_main_activity,
            getString(R.string.cant_get_location_message),
            Snackbar.LENGTH_SHORT
        )
    }

    private fun setupInfo(weatherResponse: WeatherResponse) {
        main_activity_location_text_view.text = weatherResponse.name
        val temperatureText = "${getString(R.string.temperature)}: ${weatherResponse.main.getTempInCelsius().toFormattedTemp()}"
        main_activity_temperature_text_view.text = temperatureText
        main_activity_description.text = weatherResponse.weather.first().description
        val pressureText = "${getString(R.string.pressure)}: ${weatherResponse.main.pressure} hPa"
        main_activity_pressure.text = pressureText
        val humidityText = "${getString(R.string.humidity)}: ${weatherResponse.main.humidity}%"
        main_activity_humidity.text = humidityText
        val windSpeedText = "${getString(R.string.wind_speed)}: ${weatherResponse.wind.speed} m/s"
        main_activity_wind.text = windSpeedText
        val cloudinessText = "${getString(R.string.cloudiness)}: ${weatherResponse.clouds.all} %"
        main_activity_cloudiness.text = cloudinessText
        val sunrise = Date(weatherResponse.sys.sunrise.toLong() * 1000)
        val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val sunriseText = "${getString(R.string.sunrise)}: ${sdf.format(sunrise)}"
        main_activity_sunrise.text = sunriseText
        val sunset = Date(weatherResponse.sys.sunset.toLong() * 1000)
        val sunsetText = "${getString(R.string.sunset)}: ${sdf.format(sunset)}"
        main_activity_sunset.text = sunsetText
    }

    private fun checkPermissions(): Boolean =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            permissionsRequestCode
        )
    }

    private fun requestPermissions() {
        Snackbar.make(
            constraint_layout_main_activity,
            getString(R.string.location_permission_question), Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(getString(R.string.yes)) {
                startLocationPermissionRequest()
            }
            show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionsRequestCode) {
            when {
                grantResults.isEmpty() -> Log.i("MainActivity", "User interaction was cancelled.")
                grantResults.first() == PackageManager.PERMISSION_GRANTED -> getLastLocation()
                else -> {
                    Snackbar.make(
                        constraint_layout_main_activity,
                        getString(R.string.location_permission_info), Snackbar.LENGTH_INDEFINITE
                    ).apply {
                        setAction("Grant") {
                            Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts(
                                    intentScheme,
                                    BuildConfig.APPLICATION_ID, null
                                )
                                data = uri
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }.also {
                                startActivity(it)
                            }
                        }
                        show()
                    }
                }
            }
        }
    }

    private fun getLastLocation() {
        mFusedLocationClient?.lastLocation
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    task.result?.run {
                        lat = latitude
                        lon = longitude
                        updateWeather()
                    }
                } else {
                    Snackbar.make(
                        constraint_layout_main_activity,
                        getString(R.string.no_location_detected_info),
                        Snackbar.LENGTH_SHORT
                    )
                }
            }
    }
}