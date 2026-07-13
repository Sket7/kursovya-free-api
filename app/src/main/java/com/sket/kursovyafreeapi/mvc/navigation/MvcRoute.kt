package com.sket.kursovyafreeapi.mvc.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface MvcRoute : NavKey {
    @Serializable
    data object Home : MvcRoute

    @Serializable
    data object Episodes : MvcRoute

    @Serializable
    data class EpisodeDetails(val id: Int) : MvcRoute

    @Serializable
    data object Characters : MvcRoute

    @Serializable
    data class CharacterDetails(val id: Int) : MvcRoute

    @Serializable
    data object Locations : MvcRoute

    @Serializable
    data class LocationDetails(val id: Int) : MvcRoute

    @Serializable
    data object PersonalComments : MvcRoute
}
