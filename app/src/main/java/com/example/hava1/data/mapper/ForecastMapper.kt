package com.example.hava1.data.mapper

import com.example.hava1.data.remote.dto.*
import com.example.hava1.model.CurrentWeather
import com.example.hava1.model.DailyWeather
import com.example.hava1.model.Forecast
import com.example.hava1.model.HourlyWeather

fun ForecastResponse.toForecast(): Forecast {
    val currentWeather = current?.let { item ->
        CurrentWeather(
            time = item.time,
            temperature = item.temperature.validNumber(),
            feelsLike = item.feelsLike.validNumber(),
            humidity = item.humidity.validPercentage(),
            weatherCode = item.weatherCode,
            windSpeed = item.windSpeed.validNumber()?.takeIf { it >= 0 },
            isDay =
                when (item.isDay) {
                    1 -> true
                    0 -> false
                    else -> null
                },
        )
    }

    val hourlyWeather =
        hourly
            ?.let { item ->
                item.time.orEmpty().mapIndexedNotNull { index, time ->
                    if (time.isBlank()) {
                        null
                    } else {
                        HourlyWeather(
                            time = time,
                            temperature = item.temperatures?.getOrNull(index).validNumber(),
                            weatherCode = item.weatherCodes?.getOrNull(index),
                            rainProbability =
                                item.rainProbabilities?.getOrNull(index).validPercentage(),
                        )
                    }
                }
            }
            .orEmpty()

    val dailyWeather =
        daily
            ?.let { item ->
                item.time.orEmpty().mapIndexedNotNull { index, date ->
                    if (date.isBlank()) {
                        null
                    } else {
                        DailyWeather(
                            date = date,
                            maxTemperature = item.maxTemperatures?.getOrNull(index).validNumber(),
                            minTemperature = item.minTemperatures?.getOrNull(index).validNumber(),
                            weatherCode = item.weatherCodes?.getOrNull(index),
                            rainProbability =
                                item.rainProbabilities?.getOrNull(index).validPercentage(),
                            maxWindSpeed =
                                item.maxWindSpeeds?.getOrNull(index).validNumber()?.takeIf {
                                    it >= 0
                                },
                            maxWindGust =
                                item.maxWindGusts?.getOrNull(index).validNumber()?.takeIf {
                                    it >= 0
                                },
                            precipitationSum =
                                item.precipitationSums?.getOrNull(index).validNumber()?.takeIf {
                                    it >= 0
                                },
                        )
                    }
                }
            }
            .orEmpty()

    return Forecast(
        timezone = timezone,
        current = currentWeather,
        hourly = hourlyWeather,
        daily = dailyWeather,
    )
}

private fun Double?.validNumber(): Double? {
    return this?.takeIf { it.isFinite() }
}

private fun Int?.validPercentage(): Int? {
    return this?.takeIf { it in 0..100 }
}
