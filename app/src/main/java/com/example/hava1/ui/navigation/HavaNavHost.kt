package com.example.hava1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hava1.model.CitySearchResult
import com.example.hava1.ui.city.CitySearchScreen
import com.example.hava1.ui.city.CitySearchState
import com.example.hava1.ui.forecast.ForecastScreen
import com.example.hava1.ui.forecast.ForecastState

@Composable
fun HavaNavHost(
    query: String,
    searchState: CitySearchState,
    recentCities: List<CitySearchResult>,
    forecastState: ForecastState,
    cityName: String,
    updatedAt: String?,
    onQueryChange: (String) -> Unit,
    onRetrySearch: () -> Unit,
    onSelectCity: (CitySearchResult) -> Unit,
    onRemoveRecent: (CitySearchResult) -> Unit,
    onRetryForecast: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val controller = rememberNavController()
    NavHost(controller, startDestination = "cities", modifier = modifier) {
        composable("cities") {
            CitySearchScreen(
                query = query,
                state = searchState,
                recentCities = recentCities,
                onRemoveRecent = onRemoveRecent,
                onQueryChange = onQueryChange,
                onRetry = onRetrySearch,
                onCityClick = { city ->
                    onSelectCity(city)
                    controller.navigate("forecast") { launchSingleTop = true }
                },
            )
        }
        composable("forecast") {
            ForecastScreen(
                cityName = cityName,
                state = forecastState,
                updatedAt = updatedAt,
                onBack = { controller.popBackStack() },
                onHome = {
                    onQueryChange("")
                    controller.navigate("cities") {
                        popUpTo("cities") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onRetry = onRetryForecast,
            )
        }
    }
}
