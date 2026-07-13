package com.sket.kursovyafreeapi.clean.domain

import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.EpisodeUserData
import com.sket.kursovyafreeapi.model.Location
import kotlinx.coroutines.flow.Flow

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

data class PersonalComment(val episode: Episode?, val userData: EpisodeUserData)

interface EpisodeNotesRepository {
    fun observe(episodeId: Int): Flow<EpisodeUserData?>
    fun observePersonalComments(): Flow<List<PersonalComment>>
    suspend fun save(episodeId: Int, isWatched: Boolean, rating: Int?, comment: String)
    suspend fun deleteComment(episodeId: Int)
}
