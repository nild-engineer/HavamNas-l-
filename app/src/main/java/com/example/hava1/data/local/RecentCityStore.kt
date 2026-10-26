package com.example.hava1.data.local

import android.content.Context
import com.example.hava1.domain.history.RecentCityHistory
import com.example.hava1.model.CitySearchResult
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Yalnızca seçilen şehirler cihazda tutulur; konum izni veya sunucu hesabı gerekmez. */
class RecentCityStore(context: Context) {
    private val preferences =
        context.applicationContext.getSharedPreferences("recent_cities", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val _cities = MutableStateFlow(load())
    val cities = _cities.asStateFlow()

    fun record(city: CitySearchResult) = save(RecentCityHistory.add(_cities.value, city))

    fun remove(cityId: Int) = save(RecentCityHistory.remove(_cities.value, cityId))

    private fun save(cities: List<CitySearchResult>) {
        _cities.value = cities
        preferences.edit().putString("cities", gson.toJson(cities)).apply()
    }

    private fun load(): List<CitySearchResult> = runCatching {
        val json = preferences.getString("cities", null) ?: return@runCatching emptyList()
        gson
            .fromJson(json, Array<CitySearchResult>::class.java)
            .toList()
            .filter {
                it.name.isNotBlank() &&
                    it.latitude.isFinite() &&
                    it.longitude.isFinite() &&
                    it.latitude in -90.0..90.0 &&
                    it.longitude in -180.0..180.0
            }
            .distinctBy { it.id }
            .take(RecentCityHistory.LIMIT)
    }
        .getOrDefault(emptyList())
}
