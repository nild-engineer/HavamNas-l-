package com.example.hava1.ui.forecast

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hava1.data.repository.WeatherRepository
import com.example.hava1.model.CitySearchResult
import com.example.hava1.model.Forecast
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ForecastViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val repository = WeatherRepository()

    private val _state = MutableStateFlow<ForecastState>(ForecastState.Loading)

    val state = _state.asStateFlow()
    private val _updatedAt = MutableStateFlow<String?>(null)
    val updatedAt = _updatedAt.asStateFlow()

    val cityName: String
        get() = savedStateHandle["cityName"] ?: ""

    private var forecastJob: Job? = null

    init {
        val latitude = savedStateHandle.get<Double>("latitude")
        val longitude = savedStateHandle.get<Double>("longitude")

        if (latitude != null && longitude != null) {
            loadForecast(latitude, longitude)
        }
    }

    fun selectCity(city: CitySearchResult) {
        savedStateHandle["cityName"] = city.name
        savedStateHandle["latitude"] = city.latitude
        savedStateHandle["longitude"] = city.longitude

        loadForecast(
            latitude = city.latitude,
            longitude = city.longitude,
        )
    }

    fun retry() {
        val latitude = savedStateHandle.get<Double>("latitude")
        val longitude = savedStateHandle.get<Double>("longitude")

        if (latitude == null || longitude == null) {
            _state.value =
                ForecastState.Error(
                    "Şehir bilgisi bulunamadı. Geri dönüp şehri yeniden seç.",
                )
            return
        }

        loadForecast(latitude, longitude)
    }

    private fun loadForecast(
        latitude: Double,
        longitude: Double,
    ) {
        forecastJob?.cancel()
        _state.value = ForecastState.Loading

        forecastJob = viewModelScope.launch {
            try {
                val forecast =
                    repository.getForecast(
                        latitude = latitude,
                        longitude = longitude,
                    )

                _updatedAt.value = java.time.Instant.now().toString()
                _state.value =
                    if (forecast.hasWeatherData()) {
                        ForecastState.Success(forecast)
                    } else {
                        ForecastState.Empty
                    }
            } catch (error: CancellationException) {
                throw error
            } catch (error: SocketTimeoutException) {
                _state.value =
                    ForecastState.Error(
                        "Tahmin isteği zaman aşımına uğradı. Tekrar Dene.",
                    )
            } catch (error: IOException) {
                _state.value =
                    ForecastState.Error(
                        "Tahmin servisine ulaşılamadı. İnternet " +
                            "bağlantınızı kontrol edip tekrar dene.",
                    )
            } catch (error: HttpException) {
                val message =
                    if (error.code() == 429) {
                        "Çok fazla istek gönderildi. " + "Biraz bekleyip tekrar dene."
                    } else {
                        "Tahmin servisi şu anda yanıt veremiyor. " + "Daha sonra tekrar dene."
                    }

                _state.value = ForecastState.Error(message)
            } catch (error: IllegalArgumentException) {
                _state.value =
                    ForecastState.Error(
                        "Şehir konumu geçersiz. Geri dönüp başka bir şehir seç.",
                    )
            } catch (error: Exception) {
                _state.value =
                    ForecastState.Error(
                        "Tahmin verisi işlenemedi. Tekrar Dene.",
                    )
            }
        }
    }

    private fun Forecast.hasWeatherData(): Boolean {
        val currentHasData =
            current?.let {
                it.temperature != null ||
                    it.feelsLike != null ||
                    it.humidity != null ||
                    it.weatherCode != null ||
                    it.windSpeed != null
            } ?: false

        val hourlyHasData = hourly.any {
            it.temperature != null || it.weatherCode != null || it.rainProbability != null
        }

        val dailyHasData = daily.any {
            it.maxTemperature != null ||
                it.minTemperature != null ||
                it.weatherCode != null ||
                it.rainProbability != null
        }

        return currentHasData || hourlyHasData || dailyHasData
    }
}
