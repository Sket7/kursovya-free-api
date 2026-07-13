package com.sket.kursovyafreeapi.di.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface DiRoute : NavKey {
    @Serializable data object Home : DiRoute
    @Serializable data object Episodes : DiRoute
    @Serializable data class EpisodeDetails(val id: Int) : DiRoute
    @Serializable data object Characters : DiRoute
    @Serializable data class CharacterDetails(val id: Int) : DiRoute
    @Serializable data object Locations : DiRoute
    @Serializable data class LocationDetails(val id: Int) : DiRoute
    @Serializable data object Comments : DiRoute
}

