package com.example.hava1.data.remote

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val httpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val cityApi: CityApi =
        createRetrofit(
                "https://geocoding-api.open-meteo.com/",
            )
            .create(CityApi::class.java)

    val forecastApi: ForecastApi =
        createRetrofit(
                "https://api.open-meteo.com/",
            )
            .create(ForecastApi::class.java)
}
