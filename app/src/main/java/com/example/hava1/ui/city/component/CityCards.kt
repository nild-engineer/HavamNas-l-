package com.example.hava1.ui.city.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.hava1.model.CitySearchResult
import com.example.hava1.ui.component.HomeLineIcon
import com.example.hava1.ui.theme.HomeStyle

@Composable
internal fun RecentCityCard(city: CitySearchResult, select: () -> Unit, remove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = .9f),
        modifier =
            Modifier.width(182.dp).border(1.dp, Color(0xFFDFEAF0), RoundedCornerShape(20.dp)),
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().padding(start = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Son Arama",
                    style = MaterialTheme.typography.labelSmall,
                    color = HomeStyle.Muted,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = remove,
                    modifier =
                        Modifier.semantics {
                            contentDescription = "${city.name} şehrini son aramalardan sil"
                        },
                ) {
                    HomeLineIcon("close", Modifier.size(18.dp), HomeStyle.Muted)
                }
            }
            Column(
                Modifier.fillMaxWidth()
                    .clickable(onClick = select)
                    .padding(start = 14.dp, end = 14.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    city.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    location(city),
                    style = MaterialTheme.typography.bodySmall,
                    color = HomeStyle.Muted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        "Tahmine Git",
                        style = MaterialTheme.typography.labelMedium,
                        color = HomeStyle.Blue,
                    )
                    HomeLineIcon("arrow", Modifier.size(16.dp), HomeStyle.Blue)
                }
            }
        }
    }
}

@Composable
internal fun SearchResultCard(city: CitySearchResult, select: () -> Unit) {
    Card(
        onClick = select,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    city.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    location(city),
                    style = MaterialTheme.typography.bodyMedium,
                    color = HomeStyle.Muted,
                )
            }
            HomeLineIcon("arrow", Modifier.size(22.dp))
        }
    }
}

private fun location(city: CitySearchResult): String =
    listOfNotNull(
            city.region?.takeIf { it.isNotBlank() },
            city.country?.takeIf { it.isNotBlank() },
        )
        .distinct()
        .joinToString(", ")
        .ifBlank { "Bölge bilgisi bulunmuyor" }
