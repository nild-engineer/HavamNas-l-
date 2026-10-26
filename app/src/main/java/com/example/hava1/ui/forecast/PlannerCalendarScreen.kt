package com.example.hava1.ui.forecast

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hava1.domain.planning.DayPlanner
import com.example.hava1.domain.planning.PlanLevel
import com.example.hava1.model.*
import com.example.hava1.ui.component.WeatherLoading
import com.example.hava1.ui.theme.Typography
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

private val locale = Locale.forLanguageTag("tr-TR")

private fun planColor(level: PlanLevel?): Color =
    when (level) {
        PlanLevel.OUTDOOR -> Color(0xFFBCE4C8)
        PlanLevel.PREPARED -> Color(0xFFFFE29A)
        PlanLevel.INDOOR -> Color(0xFFFFCC9C)
        PlanLevel.SEVERE -> Color(0xFFF5B9B5)
        null -> Color(0xFFE6E9ED)
    }

private fun LocalDate.label(): String =
    format(DateTimeFormatter.ofPattern("d MMMM yyyy EEEE", locale))

private fun Double?.unit(unit: String): String =
    this?.let { String.format(locale, "%.1f %s", it, unit) } ?: "Veri yok"

@Composable
fun PlannerCalendarScreen(
    cityName: String,
    state: ForecastState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    updatedAt: String?,
    modifier: Modifier = Modifier,
) {
    val forecast = (state as? ForecastState.Success)?.forecast
    val zone =
        forecast?.timezone?.let { runCatching { ZoneId.of(it) }.getOrNull() }
            ?: ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val days =
        forecast
            ?.daily
            .orEmpty()
            .mapNotNull { day ->
                runCatching { LocalDate.parse(day.date) }.getOrNull()?.let { it to day }
            }
            .filter { !it.first.isBefore(today) }
            .associate { it }
    val firstMonth = YearMonth.from(today)
    val lastMonth = YearMonth.from(days.keys.maxOrNull() ?: today).coerceAtLeast(firstMonth)
    var monthValue by rememberSaveable(cityName) { mutableStateOf(firstMonth.toString()) }
    val month = runCatching {
        YearMonth.parse(monthValue)
    }
        .getOrDefault(firstMonth)
        .coerceIn(firstMonth, lastMonth)
    var selectedDate by rememberSaveable(cityName) { mutableStateOf<String?>(null) }
    var showRules by rememberSaveable { mutableStateOf(false) }
    val scheme =
        lightColorScheme(
            primary = Color(0xFF245E50),
            background = Color(0xFFF1F7F4),
            surface = Color.White,
            onSurface = Color(0xFF172C35),
            onBackground = Color(0xFF172C35),
        )

    MaterialTheme(colorScheme = scheme, typography = Typography) {
        Surface(
            modifier.fillMaxSize(),
            color = scheme.background,
            contentColor = scheme.onBackground,
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onBack) { Text("‹ Şehirler") }
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = onRetry) { Text("Takvimi Yenile ↻") }
                    }
                    Text(
                        cityName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Havaya Göre Planla", style = MaterialTheme.typography.titleLarge)
                }
                when (state) {
                    ForecastState.Loading -> item { WeatherLoading("Takvim güncelleniyor…") }
                    ForecastState.Empty ->
                        item {
                            Text("Takvimi değerlendirecek tahmin verisi bulunamadı.")
                            Button(onClick = onRetry) { Text("Tekrar Dene") }
                        }
                    is ForecastState.Error ->
                        item {
                            Text(state.message)
                            Button(onClick = onRetry) { Text("Tekrar Dene") }
                        }
                    is ForecastState.Success -> {
                        item {
                            Text(
                                "İleri tarihlerin tahmini değişebilir. Planını her gün kontrol et; her şehir seçiminde ve yenilemede takvim güncellenir.",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            val fetched = updatedAt?.let {
                                runCatching {
                                    Instant.parse(it)
                                        .atZone(zone)
                                        .format(DateTimeFormatter.ofPattern("d MMM HH:mm", locale))
                                }
                                    .getOrNull()
                            }
                            Text(
                                "Son veri alımı: ${fetched ?: "Zaman bilgisi yok"} • ${forecast?.timezone ?: "Saat dilimi yok"}",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                        item {
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                            ) {
                                Column(
                                    Modifier.fillMaxWidth().padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        TextButton(
                                            enabled = month > firstMonth,
                                            onClick = {
                                                monthValue = month.minusMonths(1).toString()
                                            },
                                        ) {
                                            Text("‹")
                                        }
                                        Text(
                                            month.format(
                                                DateTimeFormatter.ofPattern("MMMM yyyy", locale),
                                            ),
                                            modifier = Modifier.weight(1f),
                                            fontWeight = FontWeight.Bold,
                                        )
                                        TextButton(
                                            enabled = month < lastMonth,
                                            onClick = {
                                                monthValue = month.plusMonths(1).toString()
                                            },
                                        ) {
                                            Text("›")
                                        }
                                    }
                                    Row(Modifier.fillMaxWidth()) {
                                        listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
                                            .forEach {
                                                Box(
                                                    Modifier.weight(1f),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(it, fontSize = 11.sp)
                                                }
                                            }
                                    }
                                    val offset = month.atDay(1).dayOfWeek.value - 1
                                    val weeks = (offset + month.lengthOfMonth() + 6) / 7
                                    repeat(weeks) { week ->
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        ) {
                                            repeat(7) { weekday ->
                                                val dayNumber = week * 7 + weekday - offset + 1
                                                if (dayNumber !in 1..month.lengthOfMonth()) {
                                                    Spacer(Modifier.weight(1f).height(62.dp))
                                                } else {
                                                    val date = month.atDay(dayNumber)
                                                    val assessment =
                                                        days[date]?.let { DayPlanner.assess(it) }
                                                    CalendarCell(
                                                        date,
                                                        assessment?.level,
                                                        date == today,
                                                        Modifier.weight(1f),
                                                    ) {
                                                        selectedDate = date.toString()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    val scored =
                                        days.values.count { DayPlanner.assess(it).level != null }
                                    Text(
                                        "${days.size} gün için tahmin • $scored gün değerlendirildi",
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                    Text(
                                        "Günün gerekçelerini görmek için tarihe dokun.",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        }
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(20.dp),
                            ) {
                                Column(
                                    Modifier.fillMaxWidth().padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Text("Renkler Ne Söylüyor?", fontWeight = FontWeight.Bold)
                                    PlanLevel.entries.forEach { level ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        ) {
                                            Surface(
                                                color = planColor(level),
                                                shape = RoundedCornerShape(8.dp),
                                            ) {
                                                Text(
                                                    level.number.toString(),
                                                    Modifier.padding(
                                                        horizontal = 10.dp,
                                                        vertical = 4.dp,
                                                    ),
                                                    color = Color(0xFF172C35),
                                                )
                                            }
                                            Text(
                                                level.label,
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(
                                            "—",
                                            Modifier.background(
                                                    planColor(null),
                                                    RoundedCornerShape(6.dp),
                                                )
                                                .padding(horizontal = 10.dp),
                                        )
                                        Text("Veri yok / değerlendirme için yetersiz")
                                    }
                                    TextButton(onClick = { showRules = true }) {
                                        Text("Değerlendirme Nasıl Yapılıyor?")
                                    }
                                }
                            }
                        }
                        item {
                            Text(
                                "Günlük genel değerlendirmedir; tüm saatlerin aynı olacağı anlamına gelmez. Resmî uyarı veya sel tahmini değildir.",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }
        selectedDate
            ?.takeIf { state is ForecastState.Success }
            ?.let { value ->
                val date = LocalDate.parse(value)
                val day = days[date]
                val assessment = day?.let { DayPlanner.assess(it) }
                AlertDialog(
                    containerColor = planColor(assessment?.level),
                    titleContentColor = Color(0xFF172C35),
                    textContentColor = Color(0xFF172C35),
                    tonalElevation = 0.dp,
                    onDismissRequest = { selectedDate = null },
                    title = { Text(date.label()) },
                    text = {
                        Column(
                            Modifier.verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                assessment?.level?.label ?: "Değerlendirme Yok",
                                fontWeight = FontWeight.Bold,
                            )
                            if (day == null)
                                Text(
                                    "Bu tarih için güncel tahmin verisi yok. Veri olmayan günlere puan vermiyoruz.",
                                )
                            else {
                                assessment?.reasons?.forEach { Text("• $it") }
                                Text(
                                    "${WeatherCondition.fromCode(day.weatherCode).label}\nSıcaklık: ${day.minTemperature.unit("°C")} – ${day.maxTemperature.unit("°C")}\nYağış olasılığı: ${day.rainProbability?.let { "%$it" } ?: "Veri yok"}\nToplam yağış: ${day.precipitationSum.unit("mm")}\nEn yüksek rüzgâr: ${day.maxWindSpeed.unit("km/sa")}\nRüzgâr hamlesi: ${day.maxWindGust.unit("km/sa")}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                Text(
                                    "Günlük en olumsuz koşullar hesaba katılır; uygun saatler bulunabilir. Tahmini planından önce yeniden kontrol et.",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { selectedDate = null },
                            colors =
                                ButtonDefaults.textButtonColors(contentColor = Color(0xFF172C35)),
                        ) {
                            Text("Tamam")
                        }
                    },
                )
            }
        if (showRules)
            AlertDialog(
                onDismissRequest = { showRules = false },
                title = { Text("Planlama Ölçütleri") },
                text = {
                    Column(
                        Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            "4: Fırtına kodu, rüzgâr ≥60 km/sa, hamle ≥70 km/sa, toplam yağış ≥50 mm, sıcaklık ≥40 °C veya günün en yükseği ≤−10 °C gibi sert koşullar.",
                        )
                        Text(
                            "3: Yağış olasılığı ≥%65, toplam yağış ≥5 mm, rüzgâr ≥30 km/sa, hamle ≥45 km/sa, kar/donan yağış veya düşük/yüksek sıcaklık.",
                        )
                        Text(
                            "2: Daha hafif yağış, rüzgâr veya sıcaklık nedeniyle hazırlık önerilir. 1: Bu olumsuz sinyaller yok ve temel veriler tam.",
                        )
                        Text(
                            "Eşikler uygulamanın genel planlama kurallarıdır; resmî güvenlik sınıflandırması değildir. Eksik veri olumlu hava olarak yorumlanmaz.",
                        )
                    }
                },
                confirmButton = { TextButton(onClick = { showRules = false }) { Text("Anladım") } },
            )
    }
}

@Composable
private fun CalendarCell(
    date: LocalDate,
    level: PlanLevel?,
    today: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier
            .height(62.dp)
            .background(planColor(level), shape)
            .then(if (today) Modifier.border(2.dp, Color(0xFF245E50), shape) else Modifier)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription =
                    "${date.label()}: ${level?.label ?: "Tahmin yok veya yetersiz"}"
            }
            .padding(vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            date.dayOfMonth.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF172C35),
        )
        Text(level?.number?.toString() ?: "—", fontSize = 11.sp, color = Color(0xFF172C35))
    }
}
