package com.example.hava1.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun WeatherLoading(message: String, modifier: Modifier = Modifier) {
    val symbols = listOf("☀️", "☁️", "🌧️", "❄️", "🌈")
    var index by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(800)
            index = (index + 1) % symbols.size
        }
    }
    val animation = rememberInfiniteTransition(label = "weatherLoading")
    val scale by
        animation.animateFloat(
            initialValue = 0.88f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
            label = "weatherPulse",
        )
    Column(
        modifier.fillMaxWidth().padding(vertical = 20.dp).semantics(mergeDescendants = true) {
            contentDescription = message
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(symbols[index], fontSize = 56.sp, modifier = Modifier.scale(scale))
        Text(message, style = MaterialTheme.typography.titleMedium)
        Text("Gökyüzü birazdan karşında", style = MaterialTheme.typography.bodySmall)
    }
}
