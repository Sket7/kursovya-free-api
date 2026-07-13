package com.sket.kursovyafreeapi.di.data

import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.Location

interface CatalogRepository {
    suspend fun cachedCharacters(query: CharacterQuery): CachedPage<Character>?
    suspend fun refreshCharacters(query: CharacterQuery): CachedPage<Character>
    suspend fun cachedEpisodes(query: EpisodeQuery): CachedPage<Episode>?
    suspend fun refreshEpisodes(query: EpisodeQuery): CachedPage<Episode>
    suspend fun cachedLocations(query: LocationQuery): CachedPage<Location>?
    suspend fun refreshLocations(query: LocationQuery): CachedPage<Location>
    suspend fun cachedCharacter(id: Int): Character?
    suspend fun refreshCharacter(id: Int): Character
    suspend fun cachedEpisode(id: Int): Episode?
    suspend fun refreshEpisode(id: Int): Episode
    suspend fun cachedLocation(id: Int): Location?
    suspend fun refreshLocation(id: Int): Location
    suspend fun resolveCharacters(ids: List<Int>): RelatedItems<Character>
    suspend fun resolveEpisodes(ids: List<Int>): RelatedItems<Episode>
}
