package com.example.hava1.ui.city.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hava1.ui.component.HomeLineIcon
import com.example.hava1.ui.component.SkyArtwork
import com.example.hava1.ui.theme.HomeStyle

@Composable
internal fun HomeIntroduction() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Bugünün Havası Ne Söylüyor?",
                fontSize = 25.sp,
                lineHeight = 33.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Bir şehir seç; havayı gör, gününe hazırlan.",
                style = MaterialTheme.typography.bodyMedium,
                color = HomeStyle.Muted,
            )
        }
        SkyArtwork(Modifier.width(105.dp).height(105.dp))
    }
}

@Composable
internal fun HomeInformation() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        HorizontalDivider(color = HomeStyle.Muted.copy(alpha = .18f))
        Text("Havayı Keşfet", style = MaterialTheme.typography.titleSmall)
        InformationRow(
            "bag",
            "Yanına Ne Almalısın?",
            "Seçtiğin şehrin havasına göre giyim ve eşya önerilerini gör.",
        )
        InformationRow(
            "calendar",
            "Hangi Güne Plan?",
            "Tahmin ekranındaki planlama takvimiyle uygun günleri karşılaştır.",
        )
    }
}

@Composable
private fun InformationRow(icon: String, title: String, description: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        HomeLineIcon(icon, Modifier.padding(top = 3.dp).size(20.dp), HomeStyle.Muted)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(description, style = MaterialTheme.typography.bodySmall, color = HomeStyle.Muted)
        }
    }
}
