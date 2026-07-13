package com.sket.kursovyafreeapi.clean.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface CleanRoute : NavKey {
    @Serializable data object Home : CleanRoute
    @Serializable data object Episodes : CleanRoute
    @Serializable data class EpisodeDetails(val id: Int) : CleanRoute
    @Serializable data object Characters : CleanRoute
    @Serializable data class CharacterDetails(val id: Int) : CleanRoute
    @Serializable data object Locations : CleanRoute
    @Serializable data class LocationDetails(val id: Int) : CleanRoute
    @Serializable data object Comments : CleanRoute
}
