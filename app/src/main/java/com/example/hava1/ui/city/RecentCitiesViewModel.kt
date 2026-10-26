package com.example.hava1.ui.city

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.hava1.data.local.RecentCityStore
import com.example.hava1.model.CitySearchResult

class RecentCitiesViewModel(application: Application) : AndroidViewModel(application) {
    private val store = RecentCityStore(application)
    val cities = store.cities

    fun record(city: CitySearchResult) = store.record(city)

    fun remove(city: CitySearchResult) = store.remove(city.id)
}
