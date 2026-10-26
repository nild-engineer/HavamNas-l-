package com.example.hava1.domain.history

import com.example.hava1.model.CitySearchResult

object RecentCityHistory {
    const val LIMIT = 6

    fun add(current: List<CitySearchResult>, city: CitySearchResult): List<CitySearchResult> =
        (listOf(city) + current.filterNot { it.id == city.id }).take(LIMIT)

    fun remove(current: List<CitySearchResult>, cityId: Int): List<CitySearchResult> =
        current.filterNot {
            it.id == cityId
        }
}
