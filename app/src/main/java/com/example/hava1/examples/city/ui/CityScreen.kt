package com.example.hava1.examples.city.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hava1.examples.city.model.City

@Composable
fun CityScreen(
    query: String,
    cities: List<City>,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Şehir seç",
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Şehir ara") },
                singleLine = true,
            )
        }

        if (cities.isEmpty()) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "“${query.trim()}” için şehir bulunamadı.",
                    )

                    TextButton(
                        onClick = { onQueryChange("") },
                    ) {
                        Text(text = "Aramayı temizle")
                    }
                }
            }
        }

        items(
            items = cities,
            key = { it.id },
        ) { city ->
            CityCard(
                city = city,
                onClick = {
                    Log.d(
                        "CityScreen",
                        "Tıklandı: ${city.name}",
                    )
                },
            )
        }
    }
}

@Composable
fun CityCard(
    city: City,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = city.plate.toString().padStart(2, '0'),
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = city.country,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
