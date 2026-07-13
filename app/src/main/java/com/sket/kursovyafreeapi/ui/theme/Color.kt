package com.sket.kursovyafreeapi.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

internal val PortalGreen = Color(0xFF006C4C)
internal val PortalGreenLight = Color(0xFF72DBAA)
internal val PortalGreenContainer = Color(0xFF8EF8C5)
internal val PortalGreenContainerDark = Color(0xFF005138)

internal val DimensionCyan = Color(0xFF006874)
internal val DimensionCyanLight = Color(0xFF4FD8EB)
internal val DimensionCyanContainer = Color(0xFF9EEFFD)
internal val DimensionCyanContainerDark = Color(0xFF004F58)

internal val PlumbusYellow = Color(0xFF735C00)
internal val PlumbusYellowLight = Color(0xFFE9C349)
internal val PlumbusYellowContainer = Color(0xFFFFE178)
internal val PlumbusYellowContainerDark = Color(0xFF574600)

internal val SpaceBackground = Color(0xFFF6FBF6)
internal val SpaceBackgroundDark = Color(0xFF0F1512)
internal val SpaceSurfaceDark = Color(0xFF171D19)

internal val ErrorLight = Color(0xFFBA1A1A)
internal val ErrorDark = Color(0xFFFFB4AB)

@Immutable
data class ExtendedColors(
    val portalGlow: Color,
    val watched: Color,
    val alive: Color,
    val dead: Color,
    val unknown: Color,
    val rating: Color
)

internal val LightExtendedColors = ExtendedColors(
    portalGlow = Color(0xFF00A878),
    watched = Color(0xFF18794E),
    alive = Color(0xFF2E7D32),
    dead = Color(0xFFBA1A1A),
    unknown = Color(0xFF6F7972),
    rating = Color(0xFFE0A100)
)

internal val DarkExtendedColors = ExtendedColors(
    portalGlow = Color(0xFF72F5BC),
    watched = Color(0xFF72DBAA),
    alive = Color(0xFF80DB7D),
    dead = Color(0xFFFFB4AB),
    unknown = Color(0xFFBFC9C1),
    rating = Color(0xFFFFDC73)
)
