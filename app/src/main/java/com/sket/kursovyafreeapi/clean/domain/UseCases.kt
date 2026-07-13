package com.sket.kursovyafreeapi.clean.domain

import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.Location

class LoadCharacters(private val repository: CatalogRepository) {
    suspend fun cached(query: CharacterQuery) = repository.cachedCharacters(query)
    suspend fun refresh(query: CharacterQuery) = repository.refreshCharacters(query)
}
class LoadEpisodes(private val repository: CatalogRepository) {
    suspend fun cached(query: EpisodeQuery) = repository.cachedEpisodes(query)
    suspend fun refresh(query: EpisodeQuery) = repository.refreshEpisodes(query)
}
class LoadLocations(private val repository: CatalogRepository) {
    suspend fun cached(query: LocationQuery) = repository.cachedLocations(query)
    suspend fun refresh(query: LocationQuery) = repository.refreshLocations(query)
}

data class EpisodeDetails(val episode: Episode, val characters: RelatedItems<Character>)
class GetEpisodeDetails(private val repository: CatalogRepository) {
    suspend fun cached(id: Int): EpisodeDetails? = repository.cachedEpisode(id)?.let { EpisodeDetails(it, repository.resolveCharacters(it.characterIds)) }
    suspend fun refresh(id: Int): EpisodeDetails = repository.refreshEpisode(id).let { EpisodeDetails(it, repository.resolveCharacters(it.characterIds)) }
}
data class CharacterDetails(val character: Character, val episodes: RelatedItems<Episode>)
class GetCharacterDetails(private val repository: CatalogRepository) {
    suspend fun cached(id: Int): CharacterDetails? = repository.cachedCharacter(id)?.let { CharacterDetails(it, repository.resolveEpisodes(it.episodeIds)) }
    suspend fun refresh(id: Int): CharacterDetails = repository.refreshCharacter(id).let { CharacterDetails(it, repository.resolveEpisodes(it.episodeIds)) }
}
data class LocationDetails(val location: Location, val residents: RelatedItems<Character>)
class GetLocationDetails(private val repository: CatalogRepository) {
    suspend fun cached(id: Int): LocationDetails? = repository.cachedLocation(id)?.let { LocationDetails(it, repository.resolveCharacters(it.residentIds)) }
    suspend fun refresh(id: Int): LocationDetails = repository.refreshLocation(id).let { LocationDetails(it, repository.resolveCharacters(it.residentIds)) }
}

class ObserveEpisodeNote(private val repository: EpisodeNotesRepository) { operator fun invoke(id: Int) = repository.observe(id) }
class SaveEpisodeNote(private val repository: EpisodeNotesRepository) { suspend operator fun invoke(id: Int, watched: Boolean, rating: Int?, comment: String) = repository.save(id, watched, rating, comment) }
class ObservePersonalComments(private val repository: EpisodeNotesRepository) { operator fun invoke() = repository.observePersonalComments() }
class DeletePersonalComment(private val repository: EpisodeNotesRepository) { suspend operator fun invoke(id: Int) = repository.deleteComment(id) }

data class CleanUseCases(
    val characters: LoadCharacters,
    val episodes: LoadEpisodes,
    val locations: LoadLocations,
    val episodeDetails: GetEpisodeDetails,
    val characterDetails: GetCharacterDetails,
    val locationDetails: GetLocationDetails,
    val observeNote: ObserveEpisodeNote,
    val saveNote: SaveEpisodeNote,
    val comments: ObservePersonalComments,
    val deleteComment: DeletePersonalComment
)
