package com.sket.kursovyafreeapi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LightColorScheme = lightColorScheme(
    primary = PortalGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = PortalGreenContainer,
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF002116),
    secondary = DimensionCyan,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = DimensionCyanContainer,
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF001F24),
    tertiary = PlumbusYellow,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = PlumbusYellowContainer,
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFF231B00),
    error = ErrorLight,
    background = SpaceBackground,
    onBackground = androidx.compose.ui.graphics.Color(0xFF171D19),
    surface = SpaceBackground,
    onSurface = androidx.compose.ui.graphics.Color(0xFF171D19),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFDCE5DE),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF404943),
    outline = androidx.compose.ui.graphics.Color(0xFF707973),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFFBFC9C1)
)

private val DarkColorScheme = darkColorScheme(
    primary = PortalGreenLight,
    onPrimary = androidx.compose.ui.graphics.Color(0xFF003826),
    primaryContainer = PortalGreenContainerDark,
    onPrimaryContainer = PortalGreenContainer,
    secondary = DimensionCyanLight,
    onSecondary = androidx.compose.ui.graphics.Color(0xFF00363D),
    secondaryContainer = DimensionCyanContainerDark,
    onSecondaryContainer = DimensionCyanContainer,
    tertiary = PlumbusYellowLight,
    onTertiary = androidx.compose.ui.graphics.Color(0xFF3C2F00),
    tertiaryContainer = PlumbusYellowContainerDark,
    onTertiaryContainer = PlumbusYellowContainer,
    error = ErrorDark,
    background = SpaceBackgroundDark,
    onBackground = androidx.compose.ui.graphics.Color(0xFFDFE4DF),
    surface = SpaceSurfaceDark,
    onSurface = androidx.compose.ui.graphics.Color(0xFFDFE4DF),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF404943),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFBFC9C1),
    outline = androidx.compose.ui.graphics.Color(0xFF89938C),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFF404943)
)

private val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

@Composable
fun RickAndMortyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalExtendedColors provides if (darkTheme) DarkExtendedColors else LightExtendedColors,
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

object RickAndMortyTheme {
    val colors
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val extendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current

    val typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    val spacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current
}
