package com.sket.kursovyafreeapi.mvvm.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface MvvmRoute : NavKey {
    @Serializable data object Home : MvvmRoute
    @Serializable data object Episodes : MvvmRoute
    @Serializable data class EpisodeDetails(val id: Int) : MvvmRoute
    @Serializable data object Characters : MvvmRoute
    @Serializable data class CharacterDetails(val id: Int) : MvvmRoute
    @Serializable data object Locations : MvvmRoute
    @Serializable data class LocationDetails(val id: Int) : MvvmRoute
    @Serializable data object Comments : MvvmRoute
}
