package com.sket.kursovyafreeapi.mvc.controller

import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.EpisodeUserData
import com.sket.kursovyafreeapi.model.Location
import com.sket.kursovyafreeapi.mvc.model.CatalogModel
import com.sket.kursovyafreeapi.mvc.model.EpisodeNotesModel
import com.sket.kursovyafreeapi.mvc.model.PersonalComment
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailState<T, R>(
    val item: T? = null,
    val related: List<R> = emptyList(),
    val userData: EpisodeUserData? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isOffline: Boolean = false,
    val message: String? = null
)

class EpisodeDetailsController(
    private val id: Int,
    private val catalog: CatalogModel,
    private val notes: EpisodeNotesModel,
    private val scope: CoroutineScope
) {
    private val mutableState = MutableStateFlow(DetailState<Episode, Character>())
    val state: StateFlow<DetailState<Episode, Character>> = mutableState.asStateFlow()

    fun start() {
        scope.launch { notes.observe(id).collect { mutableState.value = mutableState.value.copy(userData = it) } }
        scope.launch {
            val cached = catalog.cachedEpisode(id)
            if (cached != null) show(cached, offline = true)
            try {
                show(catalog.refreshEpisode(id), offline = false)
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                mutableState.value = mutableState.value.copy(
                    isLoading = false,
                    isOffline = cached != null,
                    message = error.userMessage()
                )
            }
        }
    }

    fun save(isWatched: Boolean, rating: Int?, comment: String) {
        scope.launch {
            mutableState.value = mutableState.value.copy(isSaving = true, message = null)
            runCatching { notes.save(id, isWatched, rating, comment) }
                .onSuccess { mutableState.value = mutableState.value.copy(isSaving = false, message = "Сохранено в Room") }
                .onFailure { mutableState.value = mutableState.value.copy(isSaving = false, message = it.userMessage()) }
        }
    }

    private suspend fun show(episode: Episode, offline: Boolean) {
        val related = catalog.resolveCharacters(episode.characterIds)
        mutableState.value = mutableState.value.copy(
            item = episode,
            related = related.items,
            isLoading = false,
            isOffline = offline || !related.isComplete,
            message = null
        )
    }
}

class CharacterDetailsController(
    private val id: Int,
    private val catalog: CatalogModel,
    private val scope: CoroutineScope
) {
    private val mutableState = MutableStateFlow(DetailState<Character, Episode>())
    val state = mutableState.asStateFlow()

    fun start() = scope.launch {
        val cached = catalog.cachedCharacter(id)
        if (cached != null) show(cached, true)
        runCatching { catalog.refreshCharacter(id) }
            .onSuccess { show(it, false) }
            .onFailure { mutableState.value = mutableState.value.copy(isLoading = false, isOffline = cached != null, message = it.userMessage()) }
    }

    private suspend fun show(character: Character, offline: Boolean) {
        val related = catalog.resolveEpisodes(character.episodeIds)
        mutableState.value = DetailState(character, related.items, isLoading = false, isOffline = offline || !related.isComplete)
    }
}

class LocationDetailsController(
    private val id: Int,
    private val catalog: CatalogModel,
    private val scope: CoroutineScope
) {
    private val mutableState = MutableStateFlow(DetailState<Location, Character>())
    val state = mutableState.asStateFlow()

    fun start() = scope.launch {
        val cached = catalog.cachedLocation(id)
        if (cached != null) show(cached, true)
        runCatching { catalog.refreshLocation(id) }
            .onSuccess { show(it, false) }
            .onFailure { mutableState.value = mutableState.value.copy(isLoading = false, isOffline = cached != null, message = it.userMessage()) }
    }

    private suspend fun show(location: Location, offline: Boolean) {
        val related = catalog.resolveCharacters(location.residentIds)
        mutableState.value = DetailState(location, related.items, isLoading = false, isOffline = offline || !related.isComplete)
    }
}

data class CommentsState(
    val items: List<PersonalComment> = emptyList(),
    val isLoading: Boolean = true
)

class PersonalCommentsController(
    private val notes: EpisodeNotesModel,
    private val scope: CoroutineScope
) {
    private val mutableState = MutableStateFlow(CommentsState())
    val state = mutableState.asStateFlow()

    fun start() = scope.launch {
        notes.observePersonalComments().collect { mutableState.value = CommentsState(it, false) }
    }

    fun deleteComment(episodeId: Int) {
        scope.launch { notes.deleteComment(episodeId) }
    }
}
