package com.example.hava1.examples.city.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.hava1.examples.city.data.CityCatalog
import com.example.hava1.examples.city.model.City
import java.util.Locale
import kotlinx.coroutines.flow.StateFlow

class CityViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val query: StateFlow<String> = savedStateHandle.getStateFlow("query", "")

    fun updateQuery(value: String) {
        savedStateHandle["query"] = value
    }

    fun filteredCities(query: String): List<City> {
        val searchText = query.trim().lowercase(Locale.forLanguageTag("tr-TR"))

        return CityCatalog.cities.filter { city ->
            city.name.lowercase(Locale.forLanguageTag("tr-TR")).contains(searchText)
        }
    }
}
