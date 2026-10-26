package com.example.hava1.model

data class Forecast(
    val timezone: String?,
    val current: CurrentWeather?,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
)

data class CurrentWeather(
    val time: String?,
    val temperature: Double?,
    val feelsLike: Double?,
    val humidity: Int?,
    val weatherCode: Int?,
    val windSpeed: Double?,
    val isDay: Boolean? = null,
)

data class HourlyWeather(
    val time: String,
    val temperature: Double?,
    val weatherCode: Int?,
    val rainProbability: Int?,
)

data class DailyWeather(
    val date: String,
    val maxTemperature: Double?,
    val minTemperature: Double?,
    val weatherCode: Int?,
    val rainProbability: Int?,
    val maxWindSpeed: Double? = null,
    val maxWindGust: Double? = null,
    val precipitationSum: Double? = null,
)
