package com.sket.kursovyafreeapi.mvi.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface MviRoute : NavKey {
    @Serializable data object Home : MviRoute
    @Serializable data object Episodes : MviRoute
    @Serializable data class EpisodeDetails(val id: Int) : MviRoute
    @Serializable data object Characters : MviRoute
    @Serializable data class CharacterDetails(val id: Int) : MviRoute
    @Serializable data object Locations : MviRoute
    @Serializable data class LocationDetails(val id: Int) : MviRoute
    @Serializable data object Comments : MviRoute
}
