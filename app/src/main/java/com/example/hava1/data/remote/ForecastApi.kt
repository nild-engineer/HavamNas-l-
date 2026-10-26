package com.example.hava1.data.remote

import com.example.hava1.data.remote.dto.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApi {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current")
        current: String =
            "temperature_2m,apparent_temperature," +
                "relative_humidity_2m,weather_code,wind_speed_10m,is_day",
        @Query("hourly") hourly: String = "temperature_2m,weather_code,precipitation_probability",
        @Query("daily")
        daily: String =
            "temperature_2m_max,temperature_2m_min," +
                "weather_code,precipitation_probability_max," +
                "wind_speed_10m_max,wind_gusts_10m_max,precipitation_sum",
        @Query("forecast_days") forecastDays: Int = 16,
        @Query("timezone") timezone: String = "auto",
        @Query("temperature_unit") temperatureUnit: String = "celsius",
        @Query("wind_speed_unit") windSpeedUnit: String = "kmh",
    ): ForecastResponse
}
