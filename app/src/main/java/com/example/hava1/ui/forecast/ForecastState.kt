package com.example.hava1.ui.forecast

import com.example.hava1.model.Forecast

sealed interface ForecastState {

    data object Loading : ForecastState

    data class Success(
        val forecast: Forecast,
    ) : ForecastState

    data object Empty : ForecastState

    data class Error(
        val message: String,
    ) : ForecastState
}
