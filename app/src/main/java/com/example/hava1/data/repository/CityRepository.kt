package com.example.hava1.data.repository

import com.example.hava1.data.remote.ApiClient
import com.example.hava1.data.remote.CityApi
import com.example.hava1.model.CitySearchResult

class CityRepository(
    private val api: CityApi = ApiClient.cityApi,
) {
    suspend fun search(query: String): List<CitySearchResult> {
        val response = api.search(name = query.trim())
        return response.results.orEmpty()
    }
}
