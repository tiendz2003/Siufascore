package com.jerry.ronaldo.siufascore.presentation.player.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.presentation.ui.Purple

@Composable
fun PlayerStatItem(
    label: String,
    value: String,
    subValue: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Purple)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                if (subValue != null) {
                    Text(
                        text = " $subValue",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatSection(title: String,leagueLogo:String, stats: List<Pair<String, String>>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Purple)
    ) {
        Column(modifier = modifier.padding(horizontal = 16.dp)) {
            Row (
             horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                AsyncImage(
                    model = leagueLogo,
                    contentDescription = "league logo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            stats.forEach { (label, value) ->
                StatRow(label = label, value = value)
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}