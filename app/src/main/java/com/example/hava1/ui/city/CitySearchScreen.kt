package com.example.hava1.ui.city

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.hava1.model.CitySearchResult
import com.example.hava1.ui.city.component.*
import com.example.hava1.ui.component.AppBrandHeader
import com.example.hava1.ui.component.HomeLineIcon
import com.example.hava1.ui.component.WeatherLoading
import com.example.hava1.ui.theme.HomeStyle
import com.example.hava1.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun CitySearchScreen(
    query: String,
    state: CitySearchState,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onCityClick: (CitySearchResult) -> Unit,
    modifier: Modifier = Modifier,
    recentCities: List<CitySearchResult> = emptyList(),
    onRemoveRecent: (CitySearchResult) -> Unit = {},
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focus = LocalFocusManager.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val browsing = query.isBlank()
    val returnHome: () -> Unit = {
        keyboard?.hide()
        focus.clearFocus()
        onQueryChange("")
        scope.launch { listState.animateScrollToItem(0) }
    }
    BackHandler(enabled = query.isNotEmpty(), onBack = returnHome)
    val select: (CitySearchResult) -> Unit = {
        keyboard?.hide()
        focus.clearFocus()
        onCityClick(it)
    }
    val scheme =
        lightColorScheme(
            primary = HomeStyle.Blue,
            background = HomeStyle.BackgroundBottom,
            surface = Color.White,
            onSurface = HomeStyle.Ink,
            onBackground = HomeStyle.Ink,
            onSurfaceVariant = HomeStyle.Muted,
        )
    MaterialTheme(colorScheme = scheme, typography = Typography) {
        Surface(modifier.fillMaxSize(), color = Color.Transparent, contentColor = HomeStyle.Ink) {
            LazyColumn(
                state = listState,
                modifier =
                    Modifier.fillMaxSize()
                        .imePadding()
                        .background(
                            Brush.verticalGradient(
                                listOf(HomeStyle.BackgroundTop, HomeStyle.BackgroundBottom),
                            ),
                        ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                item(key = "brand") { AppBrandHeader(onHome = returnHome) }
                if (browsing) item(key = "intro") { HomeIntroduction() }
                item(key = "search") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (browsing)
                            Text("Şehir Ara", style = MaterialTheme.typography.titleMedium)
                        else
                            TextButton(onClick = returnHome, contentPadding = PaddingValues(0.dp)) {
                                Text("‹ Ana Ekran")
                            }
                        OutlinedTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            modifier =
                                Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
                            label = { Text("Nerenin havası?") },
                            placeholder = { Text("Ankara, İzmir, Tokyo…") },
                            leadingIcon = { HomeLineIcon("search", Modifier.size(23.dp)) },
                            trailingIcon =
                                if (query.isNotEmpty())
                                    ({
                                        IconButton(
                                            onClick = returnHome,
                                            modifier =
                                                Modifier.semantics {
                                                    contentDescription = "Aramayı temizle"
                                                },
                                        ) {
                                            HomeLineIcon("close", Modifier.size(21.dp))
                                        }
                                    })
                                else null,
                            shape = RoundedCornerShape(18.dp),
                            singleLine = true,
                            colors =
                                OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = HomeStyle.Blue,
                                    unfocusedBorderColor = Color(0xFF729AB5),
                                ),
                        )
                        if (state == CitySearchState.Idle)
                            Text(
                                "Aramak için en az 2 karakter yaz.",
                                style = MaterialTheme.typography.bodySmall,
                                color = HomeStyle.Muted,
                            )
                    }
                }
                if (browsing && recentCities.isNotEmpty())
                    item(key = "recent") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                HomeLineIcon("history", Modifier.size(18.dp), HomeStyle.Muted)
                                Text("Son Aramalar", style = MaterialTheme.typography.titleSmall)
                            }
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(recentCities, key = { it.id }) { city ->
                                    RecentCityCard(city, { select(city) }, { onRemoveRecent(city) })
                                }
                            }
                        }
                    }
                when (state) {
                    CitySearchState.Idle ->
                        if (browsing) item(key = "information") { HomeInformation() }
                    CitySearchState.Loading ->
                        item(key = "loading") {
                            WeatherLoading("Şehrini arıyoruz…")
                            CitySearchShimmer()
                        }
                    is CitySearchState.Success -> {
                        item(key = "resultCount") {
                            Text(
                                "${state.cities.size} sonuç • Bir şehre dokun",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                        items(state.cities, key = { "result-${it.id}" }) { city ->
                            SearchResultCard(city) { select(city) }
                        }
                    }
                    CitySearchState.Empty ->
                        item(key = "empty") {
                            Text(
                                "Bu aramada şehir bulamadık. Yazımı kontrol et veya başka bir şehir dene.",
                            )
                            TextButton(onClick = returnHome) { Text("Yeni Arama Yap") }
                        }
                    is CitySearchState.Error ->
                        item(key = "error") {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(20.dp),
                            ) {
                                Column(
                                    Modifier.fillMaxWidth().padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Text(
                                        "Bağlantıda Sorun Var",
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                    Text(state.message)
                                    Button(onClick = onRetry) { Text("Tekrar Dene") }
                                }
                            }
                        }
                }
            }
        }
    }
}
