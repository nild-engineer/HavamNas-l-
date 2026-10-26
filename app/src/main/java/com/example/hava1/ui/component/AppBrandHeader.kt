package com.example.hava1.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hava1.R

@Composable
internal fun AppBrandHeader(onHome: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .heightIn(min = 48.dp)
            .clickable(
                role = Role.Button,
                onClickLabel = "Ana ekrana dön",
                onClick = onHome,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier.size(38.dp)
                .background(Color.White.copy(alpha = .75f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            SkyArtwork(Modifier.size(38.dp), animated = false)
        }
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
