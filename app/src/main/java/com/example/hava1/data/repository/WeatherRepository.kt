package com.example.hava1.data.repository

import com.example.hava1.data.mapper.toForecast
import com.example.hava1.data.remote.ApiClient
import com.example.hava1.data.remote.ForecastApi
import com.example.hava1.model.Forecast

class WeatherRepository(
    private val api: ForecastApi = ApiClient.forecastApi,
) {

    suspend fun getForecast(
        latitude: Double,
        longitude: Double,
    ): Forecast {
        require(latitude.isFinite() && latitude in -90.0..90.0) {
            "Geçersiz enlem."
        }

        require(longitude.isFinite() && longitude in -180.0..180.0) {
            "Geçersiz boylam."
        }

        return api.getForecast(
                latitude = latitude,
                longitude = longitude,
            )
            .toForecast()
    }
}
