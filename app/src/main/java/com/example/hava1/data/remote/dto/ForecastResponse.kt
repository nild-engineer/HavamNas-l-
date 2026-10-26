package com.example.hava1.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    val timezone: String?,
    val current: CurrentWeatherResponse?,
    val hourly: HourlyWeatherResponse?,
    val daily: DailyWeatherResponse?,
)

data class CurrentWeatherResponse(
    val time: String?,
    @SerializedName("temperature_2m") val temperature: Double?,
    @SerializedName("apparent_temperature") val feelsLike: Double?,
    @SerializedName("relative_humidity_2m") val humidity: Int?,
    @SerializedName("weather_code") val weatherCode: Int?,
    @SerializedName("wind_speed_10m") val windSpeed: Double?,
    @SerializedName("is_day") val isDay: Int? = null,
)

data class HourlyWeatherResponse(
    val time: List<String>?,
    @SerializedName("temperature_2m") val temperatures: List<Double?>?,
    @SerializedName("weather_code") val weatherCodes: List<Int?>?,
    @SerializedName("precipitation_probability") val rainProbabilities: List<Int?>?,
)

data class DailyWeatherResponse(
    val time: List<String>?,
    @SerializedName("temperature_2m_max") val maxTemperatures: List<Double?>?,
    @SerializedName("temperature_2m_min") val minTemperatures: List<Double?>?,
    @SerializedName("weather_code") val weatherCodes: List<Int?>?,
    @SerializedName("precipitation_probability_max") val rainProbabilities: List<Int?>?,
    @SerializedName("wind_speed_10m_max") val maxWindSpeeds: List<Double?>? = null,
    @SerializedName("wind_gusts_10m_max") val maxWindGusts: List<Double?>? = null,
    @SerializedName("precipitation_sum") val precipitationSums: List<Double?>? = null,
)
