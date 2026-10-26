package com.example.hava1.data.remote

import com.example.hava1.data.remote.dto.CitySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CityApi {

    @GET("v1/search")
    suspend fun search(
        @Query("name") name: String,
        @Query("count") count: Int = 20,
        @Query("language") language: String = "tr",
    ): CitySearchResponse
}
