package com.example.hava1.ui.forecast.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hava1.domain.recommendation.OutfitAdvisor
import com.example.hava1.domain.time.ForecastTimeline
import com.example.hava1.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

private val tr = Locale.forLanguageTag("tr-TR")

@Composable
internal fun WeatherHero(forecast: Forecast, symbol: String) {
    val current = forecast.current
    Card(
        shape = RoundedCornerShape(28.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Şimdi", style = MaterialTheme.typography.labelLarge)
                    Text(
                        current?.temperature.temperature(),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Light,
                    )
                }
                Text(symbol, fontSize = 66.sp)
            }
            Text(
                WeatherCondition.fromCode(current?.weatherCode).label,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text("Hissedilen ${current?.feelsLike.temperature()}")
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("💧 Nem")
                    Text(current?.humidity.percent(), fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f)) {
                    Text("🌬️ Rüzgâr")
                    Text(current?.windSpeed.number("km/sa"), fontWeight = FontWeight.Bold)
                }
            }
            Text(
                "Veri zamanı: ${current?.time.dateTime()}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
internal fun AdviceCard(forecast: Forecast) {
    var tick by remember(forecast) { mutableIntStateOf(0) }
    LaunchedEffect(forecast) {
        while (true) {
            delay(60_000)
            tick++
        }
    }
    val tips = remember(forecast, tick) { OutfitAdvisor.advise(forecast) }
    Card(shape = RoundedCornerShape(24.dp)) {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Bugün Yanına Almanı Tavsiye Ediyoruz",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text("Anlık hava + önümüzdeki 6 saat", style = MaterialTheme.typography.bodySmall)
            tips.forEach { tip ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(tip.icon, fontSize = 26.sp)
                    Column(Modifier.weight(1f)) {
                        Text(tip.title, fontWeight = FontWeight.SemiBold)
                        Text(tip.reason, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Text(
                "Genel önerilerdir; kişisel konforuna göre uyarlayabilirsin.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
internal fun HourlyStrip(forecast: Forecast) {
    var tick by remember(forecast) { mutableIntStateOf(0) }
    LaunchedEffect(forecast) {
        while (true) {
            delay(60_000)
            tick++
        }
    }
    val hours = remember(forecast, tick) { ForecastTimeline.upcoming(forecast) }
    if (hours.isEmpty()) {
        Text("Güncel saatlik tahmin gösterilemiyor. Veriyi yenilemeyi deneyebilirsin.")
        return
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(hours, key = { it.time }) { hour ->
            Card(shape = RoundedCornerShape(22.dp), modifier = Modifier.width(116.dp)) {
                Column(
                    Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        ForecastTimeline.parseTime(hour.time)
                            ?.format(DateTimeFormatter.ofPattern("d MMM", tr)) ?: "—",
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        ForecastTimeline.parseTime(hour.time)
                            ?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "—",
                        fontWeight = FontWeight.Bold,
                    )
                    Text(WeatherCondition.fromCode(hour.weatherCode).emoji, fontSize = 30.sp)
                    Text(hour.temperature.temperature(), fontWeight = FontWeight.SemiBold)
                    Text(
                        WeatherCondition.fromCode(hour.weatherCode).label,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        "Yağış ${hour.rainProbability.percent()}",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
internal fun DayRow(day: DailyWeather) {
    val condition = WeatherCondition.fromCode(day.weatherCode)
    val date = runCatching {
        LocalDate.parse(day.date).format(DateTimeFormatter.ofPattern("d MMM • EEE", tr))
    }
        .getOrDefault("Tarih yok")
    Card(shape = RoundedCornerShape(20.dp)) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(condition.emoji, fontSize = 28.sp)
                Text(date, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            }
            Text(condition.label, style = MaterialTheme.typography.bodyMedium)
            Text(
                "↓ ${day.minTemperature.temperature()}    ↑ ${day.maxTemperature.temperature()}    •    Yağış ${day.rainProbability.percent()}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
internal fun SectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
internal fun MessageCard(title: String, message: String, retry: () -> Unit) {
    Card(shape = RoundedCornerShape(24.dp)) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("🌦️", fontSize = 48.sp)
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(message)
            Button(onClick = retry) { Text("Tekrar Dene") }
        }
    }
}

private fun Double?.temperature() = number("°C")

private fun Double?.number(unit: String): String =
    this?.let { String.format(tr, "%.1f %s", it, unit) } ?: "Veri yok"

private fun Int?.percent(): String = this?.let { "%$it" } ?: "Veri yok"

private fun String?.dateTime(): String =
    this?.let { value ->
        runCatching {
            java.time.LocalDateTime.parse(value)
                .format(DateTimeFormatter.ofPattern("d MMM HH:mm", tr))
        }
            .getOrNull()
    } ?: "Bilgi yok"
