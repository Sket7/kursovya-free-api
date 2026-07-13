package com.sket.kursovyafreeapi.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.CharacterStatus
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.Location
import com.sket.kursovyafreeapi.ui.theme.RickAndMortyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalScreen(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = RickAndMortyTheme.colors.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        style = RickAndMortyTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Text("←", style = RickAndMortyTheme.typography.headlineMedium)
                        }
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RickAndMortyTheme.colors.background.copy(alpha = 0.96f)
                )
            )
        },
        content = content
    )
}

@Composable
fun PortalHero(
    eyebrow: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RickAndMortyTheme.shapes.extraLarge)
            .background(
                Brush.linearGradient(
                    listOf(
                        RickAndMortyTheme.colors.primary,
                        RickAndMortyTheme.colors.secondary
                    )
                )
            )
            .padding(24.dp)
    ) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .size(112.dp)
                .background(Color.White.copy(alpha = 0.12f), CircleShape)
        )
        Column(
            modifier = Modifier.fillMaxWidth(0.86f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(eyebrow.uppercase(), style = RickAndMortyTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.82f))
            Text(title, style = RickAndMortyTheme.typography.headlineLarge, color = Color.White)
            Text(description, style = RickAndMortyTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable
fun SectionTitle(title: String, supporting: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, style = RickAndMortyTheme.typography.titleLarge)
        supporting?.let { Text(it, style = RickAndMortyTheme.typography.bodySmall, color = RickAndMortyTheme.colors.onSurfaceVariant) }
    }
}

@Composable
fun NavigationCard(title: String, description: String, accent: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RickAndMortyTheme.shapes.large,
        color = RickAndMortyTheme.colors.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(12.dp).background(accent, CircleShape))
            Column(Modifier.weight(1f)) {
                Text(title, style = RickAndMortyTheme.typography.titleMedium)
                Text(description, style = RickAndMortyTheme.typography.bodySmall, color = RickAndMortyTheme.colors.onSurfaceVariant)
            }
            Text("→", style = RickAndMortyTheme.typography.titleLarge, color = accent)
        }
    }
}

@Composable
fun EpisodeResourceCard(value: Episode, onClick: () -> Unit) {
    ResourceSurface(onClick) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value.name, style = RickAndMortyTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(value.airDate, style = RickAndMortyTheme.typography.bodySmall, color = RickAndMortyTheme.colors.onSurfaceVariant)
        }
        Badge(value.code, RickAndMortyTheme.colors.secondaryContainer, RickAndMortyTheme.colors.onSecondaryContainer)
    }
}

@Composable
fun LocationResourceCard(value: Location, onClick: () -> Unit) {
    ResourceSurface(onClick) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value.name, style = RickAndMortyTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(value.dimension, style = RickAndMortyTheme.typography.bodySmall, color = RickAndMortyTheme.colors.onSurfaceVariant, maxLines = 1)
        }
        Badge(value.type.ifBlank { "Неизвестно" }, RickAndMortyTheme.colors.tertiaryContainer, RickAndMortyTheme.colors.onTertiaryContainer)
    }
}

@Composable
fun CharacterResourceCard(value: Character, image: @Composable () -> Unit, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RickAndMortyTheme.shapes.large,
        color = RickAndMortyTheme.colors.surface,
        tonalElevation = 2.dp
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(72.dp).clip(RickAndMortyTheme.shapes.medium)) { image() }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(value.name, style = RickAndMortyTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val statusColor = when (value.status) {
                        CharacterStatus.ALIVE -> RickAndMortyTheme.extendedColors.alive
                        CharacterStatus.DEAD -> RickAndMortyTheme.extendedColors.dead
                        CharacterStatus.UNKNOWN -> RickAndMortyTheme.extendedColors.unknown
                    }
                    Box(Modifier.size(8.dp).align(Alignment.CenterVertically).background(statusColor, CircleShape))
                    Text("${value.status.displayName()} · ${value.species}", style = RickAndMortyTheme.typography.bodySmall)
                }
                Text(value.location.name, style = RickAndMortyTheme.typography.bodySmall, color = RickAndMortyTheme.colors.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun StateMessage(title: String, description: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Surface(shape = RickAndMortyTheme.shapes.large, color = RickAndMortyTheme.colors.surfaceVariant.copy(alpha = 0.55f)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = RickAndMortyTheme.typography.titleMedium)
            Text(description, style = RickAndMortyTheme.typography.bodySmall, color = RickAndMortyTheme.colors.onSurfaceVariant)
            if (action != null && onAction != null) FilledTonalButton(onClick = onAction) { Text(action) }
        }
    }
}

@Composable
private fun ResourceSurface(onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RickAndMortyTheme.shapes.large,
        color = RickAndMortyTheme.colors.surface,
        tonalElevation = 2.dp
    ) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), content = content) }
}

@Composable
private fun Badge(text: String, background: Color, foreground: Color) {
    Surface(shape = CircleShape, color = background) {
        Text(text, Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = RickAndMortyTheme.typography.labelMedium, color = foreground, maxLines = 1)
    }
}

private fun CharacterStatus.displayName() = when (this) {
    CharacterStatus.ALIVE -> "Жив"
    CharacterStatus.DEAD -> "Мёртв"
    CharacterStatus.UNKNOWN -> "Неизвестно"
}
