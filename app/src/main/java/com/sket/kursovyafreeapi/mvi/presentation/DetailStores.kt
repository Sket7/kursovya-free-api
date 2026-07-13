package com.sket.kursovyafreeapi.mvi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.EpisodeUserData
import com.sket.kursovyafreeapi.model.Location
import com.sket.kursovyafreeapi.mvi.data.CatalogRepository
import com.sket.kursovyafreeapi.mvi.data.EpisodeNotesRepository
import com.sket.kursovyafreeapi.mvi.data.PersonalComment
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailState<T, R>(val item: T? = null, val related: List<R> = emptyList(), val userData: EpisodeUserData? = null, val isLoading: Boolean = true, val isSaving: Boolean = false, val isOffline: Boolean = false, val error: String? = null)
sealed interface DetailIntent { data object Retry : DetailIntent }
sealed interface EpisodeIntent { data object Retry : EpisodeIntent; data class Save(val watched: Boolean, val rating: Int?, val comment: String) : EpisodeIntent }
sealed interface EpisodeEffect { data object Saved : EpisodeEffect; data class Failed(val message: String) : EpisodeEffect }

class EpisodeDetailsStore(private val id: Int, private val repo: CatalogRepository, private val notes: EpisodeNotesRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(DetailState<Episode, Character>()); val state = mutableState.asStateFlow(); private val mutableEffect = MutableSharedFlow<EpisodeEffect>(); val effect = mutableEffect.asSharedFlow()
    init { viewModelScope.launch { notes.observe(id).collect { mutableState.value = mutableState.value.copy(userData = it) } }; load() }
    fun accept(intent: EpisodeIntent) { when (intent) { EpisodeIntent.Retry -> load(); is EpisodeIntent.Save -> save(intent) } }
    private fun load() { viewModelScope.launch { mutableState.value = mutableState.value.copy(isLoading = true, error = null); val local = repo.cachedEpisode(id); if (local != null) show(local, true); runCatching { repo.refreshEpisode(id) }.onSuccess { show(it, false) }.onFailure { mutableState.value = mutableState.value.copy(isLoading = false, isOffline = local != null, error = it.userMessage()) } } }
    private fun save(intent: EpisodeIntent.Save) { viewModelScope.launch { mutableState.value = mutableState.value.copy(isSaving = true); runCatching { notes.save(id, intent.watched, intent.rating, intent.comment) }.onSuccess { mutableState.value = mutableState.value.copy(isSaving = false); mutableEffect.emit(EpisodeEffect.Saved) }.onFailure { mutableState.value = mutableState.value.copy(isSaving = false); mutableEffect.emit(EpisodeEffect.Failed(it.userMessage())) } } }
    private suspend fun show(value: Episode, offline: Boolean) { val related = repo.resolveCharacters(value.characterIds); mutableState.value = mutableState.value.copy(item = value, related = related.items, isLoading = false, isOffline = offline || !related.isComplete, error = null) }
}

class CharacterDetailsStore(private val id: Int, private val repo: CatalogRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(DetailState<Character, Episode>()); val state = mutableState.asStateFlow(); init { load() }
    fun accept(intent: DetailIntent) { if (intent == DetailIntent.Retry) load() }
    private fun load() { viewModelScope.launch { val local = repo.cachedCharacter(id); if (local != null) show(local, true); runCatching { repo.refreshCharacter(id) }.onSuccess { show(it, false) }.onFailure { mutableState.value = mutableState.value.copy(isLoading = false, isOffline = local != null, error = it.userMessage()) } } }
    private suspend fun show(value: Character, offline: Boolean) { val related = repo.resolveEpisodes(value.episodeIds); mutableState.value = DetailState(value, related.items, isLoading = false, isOffline = offline || !related.isComplete) }
}

class LocationDetailsStore(private val id: Int, private val repo: CatalogRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(DetailState<Location, Character>()); val state = mutableState.asStateFlow(); init { load() }
    fun accept(intent: DetailIntent) { if (intent == DetailIntent.Retry) load() }
    private fun load() { viewModelScope.launch { val local = repo.cachedLocation(id); if (local != null) show(local, true); runCatching { repo.refreshLocation(id) }.onSuccess { show(it, false) }.onFailure { mutableState.value = mutableState.value.copy(isLoading = false, isOffline = local != null, error = it.userMessage()) } } }
    private suspend fun show(value: Location, offline: Boolean) { val related = repo.resolveCharacters(value.residentIds); mutableState.value = DetailState(value, related.items, isLoading = false, isOffline = offline || !related.isComplete) }
}

sealed interface CommentsIntent { data class Delete(val episodeId: Int) : CommentsIntent }
data class CommentsState(val items: List<PersonalComment> = emptyList(), val isLoading: Boolean = true)
class CommentsStore(private val notes: EpisodeNotesRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(CommentsState()); val state = mutableState.asStateFlow(); init { viewModelScope.launch { notes.observePersonalComments().collect { mutableState.value = CommentsState(it, false) } } }
    fun accept(intent: CommentsIntent) { when (intent) { is CommentsIntent.Delete -> viewModelScope.launch { notes.deleteComment(intent.episodeId) } } }
}
