package com.example.hava1.ui.forecast

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hava1.ui.component.AppBrandHeader
import kotlinx.coroutines.launch

@Composable
fun ForecastScreen(
    cityName: String,
    state: ForecastState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    updatedAt: String? = null,
    onHome: () -> Unit = onBack,
) {
    val pager = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    BackHandler(enabled = pager.currentPage == 1) { scope.launch { pager.animateScrollToPage(0) } }
    Column(modifier.fillMaxSize()) {
        AppBrandHeader(onHome, Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
        TabRow(selectedTabIndex = pager.currentPage) {
            listOf("Hava", "Planlama Takvimi").forEachIndexed { index, label ->
                Tab(
                    selected = pager.currentPage == index,
                    onClick = { scope.launch { pager.animateScrollToPage(index) } },
                    text = { Text(label) },
                )
            }
        }
        HorizontalPager(
            state = pager,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            key = { it },
        ) { page ->
            if (page == 0) WeatherDetailsScreen(cityName, state, onBack, onRetry)
            else PlannerCalendarScreen(cityName, state, onBack, onRetry, updatedAt)
        }
    }
}
