package com.example.hava1.ui.forecast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hava1.model.*
import com.example.hava1.ui.component.WeatherLoading
import com.example.hava1.ui.forecast.component.*
import com.example.hava1.ui.theme.Typography
import com.example.hava1.ui.theme.weatherPalette

@Composable
internal fun WeatherDetailsScreen(
    cityName: String,
    state: ForecastState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val forecast = (state as? ForecastState.Success)?.forecast
    val palette = weatherPalette(forecast?.current)
    val night = forecast?.current?.isDay == false
    val scheme =
        if (night)
            darkColorScheme(
                primary = palette.accent,
                background = palette.bottom,
                surface = Color(0xFF27364E),
                onSurface = Color(0xFFF2F5FC),
                onSurfaceVariant = Color(0xFFD0DBEA),
                onBackground = Color(0xFFF2F5FC),
            )
        else
            lightColorScheme(
                primary = palette.accent,
                background = palette.bottom,
                surface = Color(0xFFFCFDFD),
                onSurface = Color(0xFF172C35),
                onSurfaceVariant = Color(0xFF435B64),
                onBackground = Color(0xFF172C35),
            )
    MaterialTheme(colorScheme = scheme, typography = Typography) {
        Surface(
            modifier.fillMaxSize(),
            color = Color.Transparent,
            contentColor = scheme.onBackground,
        ) {
            LazyColumn(
                modifier =
                    Modifier.fillMaxSize()
                        .background(
                            Brush.verticalGradient(listOf(palette.top, palette.bottom)),
                        ),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onBack) { Text("‹ Şehirler") }
                        Spacer(Modifier.weight(1f))
                        if (state is ForecastState.Success) {
                            TextButton(onClick = onRetry) { Text("Yenile ↻") }
                        }
                    }
                }
                item {
                    Text(
                        cityName.ifBlank { "Hava tahmini" },
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(palette.caption, style = MaterialTheme.typography.bodyMedium)
                }
                when (state) {
                    ForecastState.Loading -> item { WeatherLoading("Havanı hazırlıyoruz…") }
                    ForecastState.Empty ->
                        item {
                            MessageCard(
                                "Henüz Tahmin Yok",
                                "Bu konum için tahmin verisi gelmedi. Başka bir şehir seçebilir veya yeniden deneyebilirsin.",
                                onRetry,
                            )
                        }
                    is ForecastState.Error ->
                        item { MessageCard("Bağlantıda sorun var", state.message, onRetry) }
                    is ForecastState.Success -> {
                        item { WeatherHero(state.forecast, palette.symbol) }
                        item { AdviceCard(state.forecast) }
                        item {
                            SectionTitle("Saat Saat", "Şehrin yerel saatine göre • yatay kaydır")
                        }
                        item { HourlyStrip(state.forecast) }
                        item {
                            SectionTitle(
                                "Önümüzdeki 7 Gün",
                                "Günün en düşük ve en yüksek sıcaklıkları",
                            )
                        }
                        if (state.forecast.daily.isEmpty()) {
                            item { Text("Günlük tahmin verisi bulunmuyor.") }
                        } else {
                            items(state.forecast.daily.take(7)) { day -> DayRow(day) }
                            if (state.forecast.daily.size < 7)
                                item {
                                    Text(
                                        "Servis ${state.forecast.daily.size} günlük veri gönderdi.",
                                    )
                                }
                        }
                        item {
                            Text(
                                "Tahminler değişebilir. Veri: Open-Meteo\nSaat dilimi: ${state.forecast.timezone ?: "Bilgi yok"}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }
    }
}
