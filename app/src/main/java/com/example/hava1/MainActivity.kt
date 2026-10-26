package com.example.hava1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hava1.ui.city.CitySearchViewModel
import com.example.hava1.ui.city.RecentCitiesViewModel
import com.example.hava1.ui.forecast.ForecastViewModel
import com.example.hava1.ui.navigation.HavaNavHost
import com.example.hava1.ui.theme.Hava1Theme

class MainActivity : ComponentActivity() {

    private val recentCitiesViewModel: RecentCitiesViewModel by lazy {
        ViewModelProvider(this)[RecentCitiesViewModel::class.java]
    }

    private val citySearchViewModel: CitySearchViewModel by lazy {
        ViewModelProvider(this)[CitySearchViewModel::class.java]
    }

    private val forecastViewModel: ForecastViewModel by lazy {
        ViewModelProvider(this)[ForecastViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "onCreate çalıştı")
        enableEdgeToEdge()

        setContent {
            Hava1Theme {
                val query by citySearchViewModel.query.collectAsStateWithLifecycle()

                val recentCities by recentCitiesViewModel.cities.collectAsStateWithLifecycle()

                val searchState by citySearchViewModel.state.collectAsStateWithLifecycle()

                val forecastState by forecastViewModel.state.collectAsStateWithLifecycle()
                val forecastUpdatedAt by forecastViewModel.updatedAt.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    HavaNavHost(
                        query = query,
                        searchState = searchState,
                        recentCities = recentCities,
                        forecastState = forecastState,
                        cityName = forecastViewModel.cityName,
                        updatedAt = forecastUpdatedAt,
                        onQueryChange = citySearchViewModel::updateQuery,
                        onRetrySearch = citySearchViewModel::retry,
                        onRemoveRecent = recentCitiesViewModel::remove,
                        onSelectCity = { city ->
                            Log.d("CityScreen", "Seçildi: ${city.name}")
                            recentCitiesViewModel.record(city)
                            forecastViewModel.selectCity(city)
                        },
                        onRetryForecast = forecastViewModel::retry,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "onStop")
    }
}
