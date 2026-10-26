package com.example.hava1.ui.city

import com.example.hava1.model.CitySearchResult

sealed interface CitySearchState {

    data object Idle : CitySearchState

    data object Loading : CitySearchState

    data class Success(
        val cities: List<CitySearchResult>,
    ) : CitySearchState

    data object Empty : CitySearchState

    data class Error(
        val message: String,
    ) : CitySearchState
}
