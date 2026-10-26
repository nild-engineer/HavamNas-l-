package com.example.hava1.model

import com.google.gson.annotations.SerializedName

data class CitySearchResult(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    @SerializedName("admin1") val region: String?,
)
