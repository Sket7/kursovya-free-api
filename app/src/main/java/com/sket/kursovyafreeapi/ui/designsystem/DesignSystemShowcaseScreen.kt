package com.sket.kursovyafreeapi.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sket.kursovyafreeapi.ui.theme.RickAndMortyTheme

@Composable
fun DesignSystemShowcaseScreen(modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
                .padding(RickAndMortyTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.large)
        ) {
            PortalHeader()
            TypographySample()
            StatusSample()
            ActionSample()
        }
    }
}

@Composable
private fun PortalHeader() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = RickAndMortyTheme.colors.primaryContainer
        ),
        shape = RickAndMortyTheme.shapes.extraLarge
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            RickAndMortyTheme.colors.primaryContainer,
                            RickAndMortyTheme.colors.secondaryContainer
                        )
                    )
                )
                .padding(RickAndMortyTheme.spacing.large)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .height(88.dp)
                    .fillMaxWidth(0.28f)
                    .background(
                        color = RickAndMortyTheme.extendedColors.portalGlow.copy(alpha = 0.42f),
                        shape = CircleShape
                    )
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)
            ) {
                Text(
                    text = "МУЛЬТИВСЕЛЕННАЯ",
                    style = RickAndMortyTheme.typography.labelLarge,
                    color = RickAndMortyTheme.colors.onPrimaryContainer
                )
                Text(
                    text = "Рик и Морти",
                    style = RickAndMortyTheme.typography.displaySmall,
                    color = RickAndMortyTheme.colors.onPrimaryContainer
                )
                Text(
                    text = "Эпизоды, персонажи и локации в одном портале.",
                    style = RickAndMortyTheme.typography.bodyLarge,
                    color = RickAndMortyTheme.colors.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun TypographySample() {
    Column(verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)) {
        Text("Типографика", style = RickAndMortyTheme.typography.headlineSmall)
        Text("Заголовки — Russo One", style = RickAndMortyTheme.typography.titleLarge)
        Text(
            text = "Основной текст набран Manrope: он остаётся читаемым в карточках, фильтрах и длинных комментариях.",
            style = RickAndMortyTheme.typography.bodyLarge,
            color = RickAndMortyTheme.colors.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusSample() {
    Column(verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)) {
        Text("Состояния", style = RickAndMortyTheme.typography.headlineSmall)
        Row(
            horizontalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)
        ) {
            StatusChip("Жив", RickAndMortyTheme.extendedColors.alive)
            StatusChip("Мёртв", RickAndMortyTheme.extendedColors.dead)
            StatusChip("Неизвестно", RickAndMortyTheme.extendedColors.unknown)
        }
        AssistChip(
            onClick = {},
            label = { Text("★ 4 из 5") }
        )
    }
}

@Composable
private fun StatusChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.14f),
        contentColor = color,
        shape = RickAndMortyTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = RickAndMortyTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ActionSample() {
    Column(verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)) {
        Text("Действия", style = MaterialTheme.typography.headlineSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {}
            ) {
                Text("Открыть портал")
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {}
            ) {
                Text("Фильтры")
            }
        }
    }
}
